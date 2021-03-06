package fr.upem.net.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class ClientEOS {

    public static final Charset UTF8_CHARSET = StandardCharsets.UTF_8;
    public static final int BUFFER_SIZE = 1024;
    public static final Logger logger = Logger.getLogger(ClientEOS.class.getName());

    private static void sendRequest(String request, SocketChannel sc) throws IOException {
        var buffer = ByteBuffer.allocate(BUFFER_SIZE);
        buffer.put(UTF8_CHARSET.encode(request));
        buffer.flip();
        sc.write(buffer);
        logger.info("Request sent");
    }

    /**
     * This method: 
     * - connect to server 
     * - writes the bytes corresponding to request in UTF8 
     * - closes the write-channel to the server 
     * - stores the bufferSize first bytes of server response 
     * - return the corresponding string in UTF8
     *
     * @param request the request to send to the server
     * @param server the server to connect to
     * @param bufferSize the number of bytes to read from the server
     * @return the UTF8 string corresponding to bufferSize first bytes of server
     *         response
     * @throws IOException if an I/O error occurs
     */
    public static String getFixedSizeResponse(String request, SocketAddress server, int bufferSize) throws IOException {
        var sc = SocketChannel.open(server);
        sendRequest(request, sc);

        sc.shutdownOutput();

        var buffer = ByteBuffer.allocate(bufferSize);
        readFully(sc, buffer);
        buffer.flip();

        sc.close();
        logger.info("Response received");
        return UTF8_CHARSET.decode(buffer).toString();
    }

    /**
     * This method: 
     * - connect to server 
     * - writes the bytes corresponding to request in UTF8 
     * - closes the write-channel to the server 
     * - reads and stores all bytes from server until read-channel is closed 
     * - return the corresponding string in UTF8
     *
     * @param request the request to send to the server
     * @param server the server to send the request to
     * @return the UTF8 string corresponding the full response of the server
     * @throws IOException if an I/O error occurs
     */
    public static String getUnboundedResponse(String request, SocketAddress server) throws IOException {
        var sc = SocketChannel.open(server);
        sendRequest(request, sc);

        sc.shutdownOutput();

        var buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
        while (readFully(sc, buffer)) {
            logger.info("Allocating more space...");
            var newBuffer = ByteBuffer.allocateDirect(buffer.capacity() * 2);
            newBuffer.put(buffer);
            buffer = newBuffer;
        }
        buffer.flip();

        sc.close();
        logger.info("Response received");
        return UTF8_CHARSET.decode(buffer).toString();
    }

    /**
     * Fills the workspace of the Bytebuffer with bytes read from sc.
     *
     * @param sc the SocketChannel to read from
     * @param buffer the ByteBuffer to fill
     * @return false if read returned -1 at some point and true otherwise
     * @throws IOException if an I/O error occurs
     */
    static boolean readFully(SocketChannel sc, ByteBuffer buffer) throws IOException {
        while (true) {
            var size = sc.read(buffer);
            if (size == 0) return true;
            else if (size == -1) return false;
        }
    }

    public static void main(String[] args) throws IOException {
        var google = new InetSocketAddress("www.google.fr", 80);
        //System.out.println(getFixedSizeResponse("GET / HTTP/1.1\r\nHost: www.google.fr\r\n\r\n", google, 512));
        System.out.println(getUnboundedResponse("GET / HTTP/1.1\r\nHost: www.google.fr\r\n\r\n", google));
    }
}
