package fr.upem.net.tcp.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class HTTPReader {

    private final Charset ASCII_CHARSET = StandardCharsets.US_ASCII;
    private final SocketChannel sc;
    private final ByteBuffer buffer;

    public HTTPReader(SocketChannel sc, ByteBuffer buffer) {
        this.sc = sc;
        this.buffer = buffer;
    }

    /**
     * @return The ASCII string terminated by CRLF without the CRLF
     *         <p>
     *         The method assume that buffer is in write mode and leaves it in
     *         write mode The method process the data from the buffer and if necessary
     *         will read more data from the socket.
     * @throws HTTPException if the connection is closed before a line
     *                     could be read
     */
    public String readLineCRLF() throws IOException {
        var line = new StringBuilder();
        while (true) {
            buffer.flip();
            byte b = '\0';
            while (buffer.hasRemaining()) {
                var prev = b;
                b = buffer.get();
                if (prev == '\r' && b == '\n') {
                    buffer.compact();
                    return line.substring(0, line.length() - 1);
                }
                line.append((char) b);
            }
            buffer.clear();
            if (sc.read(buffer) == -1) throw new HTTPException("Connection closed");
        }
    }

//    public String readLineCRLF() throws IOException {
//        var line = new StringBuilder();
//        while (true) {
//            buffer.flip();
//            line.append(ASCII_CHARSET.decode(buffer));
//            var lineCRLF = line.indexOf("\r\n");
//            buffer.clear();
//            if (lineCRLF != -1) { // CRLF found
//                var rest = line.substring(lineCRLF + 2);
//                if (rest.startsWith("\r\n")) { // CRLF found
//                    buffer.put(StandardCharsets.UTF_8.encode(rest.substring(2)));
//                } else {
//                    buffer.put(ASCII_CHARSET.encode(rest));
//                }
//                return line.substring(0, lineCRLF);
//            }
//            if (sc.read(buffer) == -1) throw new HTTPException("Connection closed");
//        }
//    }

    /**
     * @return The HTTPHeader object corresponding to the header read
     * @throws IOException HTTPException if the connection is closed before a header
     *                     could be read or if the header is ill-formed
     */
    public HTTPHeader readHeader() throws IOException {
        var statusLine = readLineCRLF();
        var headerToValues = new HashMap<String, String>();
        String headerLine;
        while (true) {
            headerLine = readLineCRLF();
            if (headerLine.isEmpty()) break;
            var header = headerLine.split(":");
            headerToValues.merge(header[0].toLowerCase(), header[1], (a, b) -> a + "; " + b);
        }
        return HTTPHeader.create(statusLine, headerToValues);
    }

    /**
     * @param size the amount of bytes to read
     * @return a ByteBuffer in write mode containing size bytes read on the socket
     *         <p>
     *         The method assume that buffer is in write mode and leaves it in
     *         write mode The method process the data from the buffer and if necessary
     *         will read more data from the socket.
     * @throws IOException HTTPException is the connection is closed before all
     *                     bytes could be read
     */
    public ByteBuffer readBytes(int size) throws IOException {
        var bytes = ByteBuffer.allocate(size);
        buffer.flip();
        bytes.put(buffer);
        while (true) {
            var read = sc.read(buffer);
            if (read == 0) break;
            else if (read == -1) throw new HTTPException("Connection closed");
        }
        return bytes;
    }

    /**
     * @return a ByteBuffer in write-mode containing a content read in chunks mode
     * @throws IOException HTTPException if the connection is closed before the end
     *                     of the chunks if chunks are ill-formed
     */

    public ByteBuffer readChunks() throws IOException {
        // TODO
        return null;
    }

    public static void main(String[] args) throws IOException {
        var charsetASCII = StandardCharsets.US_ASCII;
        var request = "GET / HTTP/1.1\r\nHost: www.w3.org\r\n\r\n";
        var sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("www.w3.org", 80));
        sc.write(charsetASCII.encode(request));
        var buffer = ByteBuffer.allocate(50);
        var reader = new HTTPReader(sc, buffer);
        System.out.println(reader.readLineCRLF());
        System.out.println(reader.readLineCRLF());
        System.out.println(reader.readLineCRLF());
        sc.close();

        buffer = ByteBuffer.allocate(50);
        sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("www.w3.org", 80));
        reader = new HTTPReader(sc, buffer);
        sc.write(charsetASCII.encode(request));
        System.out.println(reader.readHeader());
        sc.close();
//
//        buffer = ByteBuffer.allocate(50);
//        sc = SocketChannel.open();
//        sc.connect(new InetSocketAddress("www.w3.org", 80));
//        reader = new HTTPReader(sc, buffer);
//        sc.write(charsetASCII.encode(request));
//        var header = reader.readHeader();
//        System.out.println(header);
//        var content = reader.readBytes(header.getContentLength());
//        content.flip();
//        System.out.println(header.getCharset().get().decode(content));
//        sc.close();
//
//        buffer = ByteBuffer.allocate(50);
//        request = "GET / HTTP/1.1\r\nHost: www.u-pem.fr\r\n\r\n";
//        sc = SocketChannel.open();
//        sc.connect(new InetSocketAddress("www.u-pem.fr", 80));
//        reader = new HTTPReader(sc, buffer);
//        sc.write(charsetASCII.encode(request));
//        header = reader.readHeader();
//        System.out.println(header);
//        content = reader.readChunks();
//        content.flip();
//        System.out.println(header.getCharset().get().decode(content));
//        sc.close();
    }
}
