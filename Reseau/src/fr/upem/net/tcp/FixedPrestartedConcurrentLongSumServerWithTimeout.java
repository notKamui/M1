package fr.upem.net.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

public class FixedPrestartedConcurrentLongSumServerWithTimeout {

    private static final Logger logger = Logger.getLogger(FixedPrestartedConcurrentLongSumServerWithTimeout.class.getName());
    private static final int MAX_CLIENTS = 20;
    private final int tickInterval;

    private final ServerSocketChannel serverSocketChannel;
    private final int timeout;

    public FixedPrestartedConcurrentLongSumServerWithTimeout(int port, int timeout) throws IOException {
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(port));
        if (timeout < 0) throw new IllegalArgumentException("timeout must be positive");
        this.timeout = timeout;
        this.tickInterval = timeout / 4;
        logger.info(this.getClass().getName() + " starts on port " + port);
    }

    static boolean readFully(SocketChannel sc, ByteBuffer buffer) throws IOException {
        while (true) {
            var size = sc.read(buffer);
            if (size == 0) return true;
            else if (size == -1) return false;
        }
    }

    public static void main(String[] args) throws NumberFormatException, IOException, InterruptedException {
        var server = new FixedPrestartedConcurrentLongSumServerWithTimeout(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        server.launch();
    }

    /**
     * Iterative server main loop
     */
    public void launch() {
        var threadDataList = Stream
            .generate(ThreadData::new)
            .limit(MAX_CLIENTS)
            .toList();

        var manager = new Thread(() -> managerThread(threadDataList));
        manager.setDaemon(true);
        manager.start();

        var pool = ThreadPool.create(threadDataList, this::serverThread);
        pool.start();

        new Thread(() -> inputThread(pool, threadDataList)).start();

        logger.info("Server started");
    }

    private void inputThread(ThreadPool pool, List<ThreadData> threadDataList) {
        var scanner = new Scanner(System.in);
        while (!Thread.interrupted()) {
            var command = scanner.nextLine();
            switch (command.toUpperCase()) {
                case "INFO" -> {
                    var connected = threadDataList.stream().filter(ThreadData::connected).count();
                    logger.info("Active clients: " + connected);
                }

                case "SHUTDOWN" -> {
                    logger.info("Shutting down softly...");
                    try {
                        serverSocketChannel.close();
                    } catch (IOException e) {
                        // Do nothing
                    }
                    return;
                }

                case "SHUTDOWNNOW" -> {
                    logger.info("Shutting down now...");
                    pool.interrupt();
                    threadDataList.forEach(ThreadData::close);
                    return;
                }

                default -> logger.warning("Unknown command: " + command);
            }
        }
    }

    private void managerThread(List<ThreadData> threadDataList) {
        while (!Thread.interrupted()) {
            try {
                threadDataList.forEach(data -> data.closeIfInactive(timeout));
                Thread.sleep(tickInterval);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    private void serverThread(ThreadData data) {
        while (!Thread.interrupted()) {
            SocketChannel client;
            SocketAddress clientAddress;
            try {
                try {
                    client = serverSocketChannel.accept();
                    data.setSocketChannel(client);
                    clientAddress = client.getRemoteAddress();
                    logger.info("Connection accepted from " + clientAddress);
                } catch (IOException e) {
                    logger.info("Server interrupted");
                    return;
                }
                try {
                    serve(data);
                } catch (ClosedByInterruptException e) {
                    logger.info("Server interrupted by shutdown-now");
                    data.close();
                    return;
                } catch (AsynchronousCloseException ace) {
                    logger.info("Closed connection with " + clientAddress + " due to timeout");
                } catch (IOException ioe) {
                    logger.log(Level.SEVERE, "Connection terminated with client by IOException", ioe.getCause());
                }
            } finally {
                data.close();
            }
        }
    }

    /**
     * Treat the connection sc applying the protocol. All IOException are thrown
     *
     * @param data the thread data
     * @throws IOException if an I/O error occurs
     */
    private void serve(ThreadData data) throws IOException {
        var sc = data.client;
        var buffer = ByteBuffer.allocate(Integer.BYTES);
        while (true) {
            logger.info("Waiting for input");
            buffer.clear();
            if (!readFully(sc, buffer)) {
                logger.info("Connection closed by client");
                break;
            }
            data.tick();
            buffer.flip();

            if (buffer.remaining() < Integer.BYTES) {
                logger.warning("Received malformed message, ignoring");
                continue;
            }
            var amount = buffer.getInt();

            var operands = ByteBuffer.allocate(amount * Long.BYTES);
            if (!readFully(sc, operands)) {
                logger.info("Connection closed by client");
                break;
            }
            data.tick();
            operands.flip();
            if (operands.remaining() < amount * Long.BYTES) {
                logger.warning("Received malformed message, ignoring");
                continue;
            }
            var sum = 0L;
            for (int i = 0; i < amount; i++) {
                sum += operands.getLong();
            }

            operands.clear();
            operands.putLong(sum);
            operands.flip();
            sc.write(operands);
        }
    }

    private static class ThreadPool {
        private final ArrayList<Thread> threads = new ArrayList<>();

        private ThreadPool(List<ThreadData> threadDataList, Consumer<ThreadData> task) {
            threadDataList.forEach(data -> threads.add(new Thread(() -> task.accept(data))));
        }

        public static ThreadPool create(List<ThreadData> threadDataList, Consumer<ThreadData> task) {
            return new ThreadPool(threadDataList, task);
        }

        public void start() {
            threads.forEach(Thread::start);
        }

        public void interrupt() {
            threads.forEach(Thread::interrupt);
        }
    }

    private class ThreadData {
        private final Object lock = new Object();
        private SocketChannel client;
        private int ticker = 0;

        void setSocketChannel(SocketChannel client) {
            synchronized (lock) {
                this.client = requireNonNull(client);
                this.ticker = 0;
            }
        }

        void tick() {
            synchronized (lock) {
                ticker = 0;
            }
        }

        void close() {
            synchronized (lock) {
                if (client == null) return;
                try {
                    client.close();
                    client = null;
                } catch (IOException e) {
                    // Do nothing
                }
            }
        }

        boolean connected() {
            synchronized (lock) {
                return client != null;
            }
        }

        void closeIfInactive(int timeout) {
            synchronized (lock) {
                if (ticker >= timeout / tickInterval) {
                    close();
                } else {
                    ticker++;
                }
            }
        }
    }
}