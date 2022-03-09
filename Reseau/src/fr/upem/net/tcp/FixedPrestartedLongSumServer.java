package fr.upem.net.tcp;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FixedPrestartedLongSumServer {

    private static final Logger logger = Logger.getLogger(FixedPrestartedLongSumServer.class.getName());
    private final ServerSocketChannel serverSocketChannel;
    private final int maxClients;

    public FixedPrestartedLongSumServer(int port, int maxClients) throws IOException {
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(port));
        if (maxClients < 1) {
            throw new IllegalArgumentException("maxClient must be strictly positive");
        }
        this.maxClients = maxClients;
        logger.info(this.getClass().getName() + " starts on port " + port);
    }

    /**
     * Iterative server main loop
     */
    public void launch() {
        logger.info("Server started");
        ThreadPool.create(maxClients, this::serverThread).start();
    }

    private void serverThread() {
        while (!Thread.interrupted()) {
            SocketChannel client = null;
            try {
                client = serverSocketChannel.accept();
                logger.info("Connection accepted from " + client.getRemoteAddress());
                serve(client);
            } catch (IOException ioe) {
                logger.log(Level.SEVERE, "Connection terminated with client by IOException", ioe.getCause());
            } finally {
                silentlyClose(client);
            }
        }
    }

    /**
     * Treat the connection sc applying the protocol. All IOException are thrown
     *
     * @param sc the connection to treat
     * @throws IOException if an I/O error occurs
     */
    private void serve(SocketChannel sc) throws IOException {
        var buffer = ByteBuffer.allocate(Integer.BYTES);
        while (true) {
            logger.info("Waiting for input");
            buffer.clear();
            if (!readFully(sc, buffer)) {
                logger.info("Connection closed by client");
                break;
            }
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

    /**
     * Close a SocketChannel while ignoring IOException
     *
     * @param sc the channel to close
     */

    private void silentlyClose(Closeable sc) {
        if (sc != null) {
            try {
                sc.close();
            } catch (IOException e) {
                // Do nothing
            }
        }
    }

    static boolean readFully(SocketChannel sc, ByteBuffer buffer) throws IOException {
        while (true) {
            logger.info("Reading");
            var size = sc.read(buffer);
            if (size == 0) return true;
            else if (size == -1) return false;
            logger.info("Read " + size + " bytes");
        }
    }

    public static void main(String[] args) throws NumberFormatException, IOException, InterruptedException {
        var server = new FixedPrestartedLongSumServer(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        server.launch();
    }

    private static class ThreadPool {
        private final ArrayList<Thread> threads = new ArrayList<>();

        private ThreadPool(int nbThreads, Runnable task) {
            for (int i = 0; i < nbThreads; i++) {
                threads.add(new Thread(task));
            }
        }

        public static ThreadPool create(int nbThreads, Runnable task) {
            return new ThreadPool(nbThreads, task);
        }

        public void start() {
            threads.forEach(Thread::start);
        }
    }
}