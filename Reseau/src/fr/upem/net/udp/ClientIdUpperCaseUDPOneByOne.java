package fr.upem.net.udp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

public class ClientIdUpperCaseUDPOneByOne {

    private static final Logger LOGGER = Logger.getLogger(ClientIdUpperCaseUDPOneByOne.class.getName());
    private static final Charset UTF8 = StandardCharsets.UTF_8;
    private static final int BUFFER_SIZE = 1024;
    private final String inFilename;
    private final String outFilename;
    private final long timeout;
    private final InetSocketAddress server;
    private final DatagramChannel dc;
    private final SynchronousQueue<Response> queue = new SynchronousQueue<>();

    public ClientIdUpperCaseUDPOneByOne(
        String inFilename,
        String outFilename,
        long timeout,
        InetSocketAddress server
    ) throws IOException {
        this.inFilename = Objects.requireNonNull(inFilename);
        this.outFilename = Objects.requireNonNull(outFilename);
        this.timeout = timeout;
        this.server = server;
        this.dc = DatagramChannel.open();
        dc.bind(null);
    }

    public static void usage() {
        System.out.println("Usage : ClientIdUpperCaseUDPOneByOne in-filename out-filename timeout host port ");
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length != 5) {
            usage();
            return;
        }

        var inFilename = args[0];
        var outFilename = args[1];
        var timeout = Long.parseLong(args[2]);
        var server = new InetSocketAddress(args[3], Integer.parseInt(args[4]));

        // Create client with the parameters and launch it
        new ClientIdUpperCaseUDPOneByOne(inFilename, outFilename, timeout, server).launch();
    }

    private void listenerThreadRun() {
        while (!Thread.interrupted()) {
            var buffer = ByteBuffer.allocate(BUFFER_SIZE);
            try {
                dc.receive(buffer);
                buffer.flip();
                var id = buffer.getLong();
                var message = UTF8.decode(buffer).toString();
                queue.put(new Response(id, message));
            } catch (AsynchronousCloseException | InterruptedException e) {
                return;
            } catch (IOException e) {
                LOGGER.severe(e.getMessage());
            }
            buffer.clear();
        }
    }

    private void sendMessage(String line, long id, ByteBuffer buffer) throws IOException {
        LOGGER.info("Sending: " + line);
        buffer.putLong(id);
        buffer.put(UTF8.encode(line));
        buffer.flip();
        dc.send(buffer, server);
        buffer.clear();
    }

    private Response waitResponse(int id) throws InterruptedException {
        Response response;
        var timeout = this.timeout;
        do {
            var start = System.currentTimeMillis();
            response = queue.poll(timeout, TimeUnit.MILLISECONDS);
            timeout -= System.currentTimeMillis() - start;
        } while (response != null && response.id() != id && timeout > 0);
        if (response != null) LOGGER.info("Received: " + response.message() + " with id " + response.id());
        return response;
    }

    public void launch() throws IOException, InterruptedException {
        try {
            var listenerThread = new Thread(this::listenerThreadRun);
            listenerThread.start();

            // Read all lines of inFilename opened in UTF-8
            var lines = Files.readAllLines(Path.of(inFilename), UTF8);

            var buffer = ByteBuffer.allocate(BUFFER_SIZE);
            var upperCaseLines = new ArrayList<String>();

            for (int id = 0; id < lines.size(); id++) {
                try {
                    var line = lines.get(id);
                    Response response;
                    do {
                        sendMessage(line, id, buffer);
                        response = waitResponse(id);
                    } while (response == null || response.id() != id);
                    upperCaseLines.add(response.message());
                } catch (IOException e) {
                    LOGGER.severe(e.getMessage());
                }
            }

            listenerThread.interrupt();
            Files.write(Paths.get(outFilename), upperCaseLines, UTF8, CREATE, WRITE, TRUNCATE_EXISTING);
        } finally {
            dc.close();
        }
    }

    private record Response(long id, String message) {
    }
}
