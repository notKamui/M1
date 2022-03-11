package fr.upem.net.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.Objects.requireNonNull;

public class FixedPrestartedLongSumServerWithTimeout {

    private static final Logger logger = Logger.getLogger(FixedPrestartedLongSumServerWithTimeout.class.getName());
    private static final int MAX_CLIENTS = 20;
    private static final int TICK_INTERVAL = 500;

    private final ServerSocketChannel serverSocketChannel;
    private final int timeout;

    public FixedPrestartedLongSumServerWithTimeout(int port, int timeout) throws IOException {
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(port));
        if (timeout < 0) throw new IllegalArgumentException("timeout must be positive");
        this.timeout = timeout;
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
        var server = new FixedPrestartedLongSumServerWithTimeout(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        server.launch();
    }

    /**
     * Iterative server main loop
     */
    public void launch() {
        logger.info("Server started");
        var threadDataList = new ArrayList<ThreadData>();
        for (int i = 0; i < MAX_CLIENTS; i++) {
            threadDataList.add(new ThreadData());
        }
        new Thread(() -> managerThread(threadDataList)).start();
        ThreadPool.create(threadDataList, this::serverThread).start();
    }

    private void managerThread(List<ThreadData> threadDataList) {
        while (!Thread.interrupted()) {
            try {
                threadDataList.forEach(data -> data.closeIfInactive(timeout));
                Thread.sleep(TICK_INTERVAL);
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
    }

    private static class ThreadData {
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
                if (client != null) {
                    try {
                        client.close();
                    } catch (IOException e) {
                        // Do nothing
                    }
                }
            }
        }

        void closeIfInactive(int timeout) {
            synchronized (lock) {
                if (ticker >= timeout / TICK_INTERVAL) {
                    close();
                } else {
                    ticker++;
                }
            }
        }
    }
}