package fr.upem.net.tcp.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.logging.Logger;

import static java.util.Objects.requireNonNull;

public final class HTTPClient {
    private final static Logger LOGGER = Logger.getLogger(HTTPClient.class.getName());
    private final static int HTTP_PORT = 80;
    private final static int BUFFER_SIZE = 65536;
    private final SocketChannel server;
    private final String hostname;
    private final HTTPReader reader;

    public HTTPClient(String server) throws IOException {
        requireNonNull(server);
        this.server = SocketChannel.open();
        this.server.connect(new InetSocketAddress(server, HTTP_PORT));
        this.hostname = server;
        this.reader = new HTTPReader(this.server, ByteBuffer.allocateDirect(BUFFER_SIZE));
    }

    public Optional<String> getResource(String resource) throws IOException {
        requireNonNull(resource);
        server.write(ByteBuffer.wrap(("GET " + resource + " HTTP/1.1\r\nHost: " + hostname + "\r\n\r\n").getBytes()));

        var header = reader.readHeader();

        var code = header.getCode();
        LOGGER.info("HTTP code: " + code);
        if (code == 301 || code == 302) return redirect(header.getFields().getOrDefault("location", ""));
        if (code >= 500) throw new HTTPException("Server error: " + code);

        var contentType = header.getContentType();
        if (contentType.isEmpty() || !contentType.get().equals("text/html"))
            return Optional.empty();
        var cs = header.getCharset().orElse(StandardCharsets.UTF_8);

        ByteBuffer content;
        if (header.isChunkedTransfer())
            content = reader.readChunks().flip();
        else
            content = reader.readBytes(header.getContentLength()).flip();

        return Optional.of(cs.decode(content).toString());
    }

    public Optional<String> redirect(String location) throws IOException {
        requireNonNull(location);
        LOGGER.info("Redirecting to " + location);
        var redirectUrl = new URL(location);
        var host = redirectUrl.getHost();
        if (!host.equals(hostname))
            return new HTTPClient(host).getResource(redirectUrl.getPath());
        else
            return getResource(redirectUrl.getPath());
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("Usage: java HTTPClient <server> <resource>");
            System.exit(1);
        }

        var server = args[0];
        var resource = args[1];

        var client = new HTTPClient(server);
        var body = client.getResource(resource);
        body.ifPresentOrElse(System.out::println, () -> System.out.println("No body"));
    }
}
