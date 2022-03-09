package fr.upem.net.tcp;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OnDemandConcurrentLongSumServer {

    private static final Logger logger = Logger.getLogger(OnDemandConcurrentLongSumServer.class.getName());
    private static final int BUFFER_SIZE = 1024;
    private final ServerSocketChannel serverSocketChannel;

    public OnDemandConcurrentLongSumServer(int port) throws IOException {
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(port));
        logger.info(this.getClass().getName() + " starts on port " + port);
    }

    /**
     * Iterative server main loop
     *
     * @throws IOException if an I/O error occurs
     */
    public void launch() throws IOException {
        logger.info("Server started");
        while (!Thread.interrupted()) {
            SocketChannel client = serverSocketChannel.accept();
            serverThread(client).start();
        }
    }

    private Thread serverThread(SocketChannel client) {
        return new Thread(() -> {
            try {
                logger.info("Connection accepted from " + client.getRemoteAddress());
                serve(client);
            } catch (IOException ioe) {
                logger.log(Level.SEVERE, "Connection terminated with client by IOException", ioe.getCause());
            } finally {
                silentlyClose(client);
            }
        });
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
     * Close a SocketChannel while ignoring IOExecption
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

    public static void main(String[] args) throws NumberFormatException, IOException {
        var server = new OnDemandConcurrentLongSumServer(Integer.parseInt(args[0]));
        server.launch();
    }
}