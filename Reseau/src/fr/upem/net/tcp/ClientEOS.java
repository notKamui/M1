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

    private static void sendRequest(String request, SocketChannel sc, ByteBuffer buffer) throws IOException {
        buffer.put(UTF8_CHARSET.encode(request));
        buffer.flip();
        sc.write(buffer);
        logger.info("Request sent");
        buffer.clear();
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
        var buffer = ByteBuffer.allocate(bufferSize);
        var sc = SocketChannel.open(server);
        sendRequest(request, sc, buffer);
        while (buffer.hasRemaining() && sc.read(buffer) != -1);
        sc.close();
        buffer.flip();
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
        var buffer = ByteBuffer.allocate(BUFFER_SIZE);
        var sc = SocketChannel.open(server);
        sendRequest(request, sc, buffer);
        while (readFully(sc, buffer)) {
            if (buffer.hasRemaining()) continue;
            var newBuffer = ByteBuffer.allocate(buffer.capacity() * 2);
            newBuffer.put(buffer);
            buffer = newBuffer;
        }
        sc.close();
        buffer.flip();
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
        var size = 0;
        do {
            size = sc.read(buffer);
        } while (size != -1 && buffer.hasRemaining());
        return size != -1;
    }

    public static void main(String[] args) throws IOException {
        var google = new InetSocketAddress("www.google.fr", 80);
        //System.out.println(getFixedSizeResponse("GET / HTTP/1.1\r\nHost: www.google.fr\r\n\r\n", google, 512));
        System.out.println(getUnboundedResponse("GET / HTTP/1.1\r\nHost: www.google.fr\r\n\r\n", google));
    }
}
