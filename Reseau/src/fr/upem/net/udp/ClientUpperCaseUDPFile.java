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
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static java.nio.file.StandardOpenOption.*;

public class ClientUpperCaseUDPFile {
    private final static Logger LOGGER = Logger.getLogger(ClientUpperCaseUDPFile.class.getName());
    private final static Charset UTF8 = StandardCharsets.UTF_8;
    private final static int BUFFER_SIZE = 1024;

    private static void usage() {
        System.out.println("Usage : ClientUpperCaseUDPFile in-filename out-filename timeout host port ");
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length != 5) {
            usage();
            return;
        }

        var inFilename = args[0];
        var outFilename = args[1];
        var timeout = Integer.parseInt(args[2]);
        var server = new InetSocketAddress(args[3], Integer.parseInt(args[4]));
        var cs = StandardCharsets.UTF_8;

        // Read all lines of inFilename opened in UTF-8
        var lines = Files.readAllLines(Path.of(inFilename), UTF8);
        var upperCaseLines = new ArrayList<String>();

        var queue = new ArrayBlockingQueue<String>(1);

        try (var channel = DatagramChannel.open()) {
            channel.bind(null);
            var thread = new Thread(() -> {
                while (!Thread.interrupted()) {
                    var buffer = ByteBuffer.allocate(BUFFER_SIZE);
                    try {
                        channel.receive(buffer);
                        buffer.flip();
                        queue.put(cs.decode(buffer).toString());
                    } catch (AsynchronousCloseException | InterruptedException e) {
                        return;
                    } catch (IOException e) {
                        LOGGER.severe(e.getMessage());
                    }
                    buffer.clear();
                }
            });
            thread.start();
            for (var line : lines) {
                try {
                    String msg;
                    do {
                        LOGGER.info("Sending: " + line);
                        channel.send(cs.encode(line), server);
                        msg = queue.poll(timeout, TimeUnit.MILLISECONDS);
                    } while (msg == null);
                    LOGGER.info("Received: " + msg);
                    upperCaseLines.add(msg);
                } catch (IOException e) {
                    LOGGER.severe(e.getMessage());
                }
            }
            thread.interrupt();
        }

        // Write upperCaseLines to outFilename in UTF-8
        Files.write(Path.of(outFilename), upperCaseLines, UTF8, CREATE, WRITE, TRUNCATE_EXISTING);
    }
}