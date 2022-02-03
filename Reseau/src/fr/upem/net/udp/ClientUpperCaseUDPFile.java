package fr.upem.net.udp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
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

    private record Packet(InetSocketAddress address, int size, String message) {
    }

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
        var buffer = ByteBuffer.allocate(BUFFER_SIZE);

        // Read all lines of inFilename opened in UTF-8
        var lines = Files.readAllLines(Path.of(inFilename), UTF8);
        var upperCaseLines = new ArrayList<String>();

        var channel = DatagramChannel.open();
        channel.bind(null);
        for (var line : lines) {
            LOGGER.info("Sending : " + line);
            var queue = new ArrayBlockingQueue<Packet>(1);
            Packet packet;
            do {
                var bytes = cs.encode(line);
                channel.send(bytes, server);
                var listener = new Thread(() -> {
                    try {
                        var sender = (InetSocketAddress) channel.receive(buffer);
                        buffer.flip();
                        queue.put(new Packet(sender, buffer.remaining(), cs.decode(buffer).toString()));
                        buffer.clear();
                    } catch (IOException e) {
                        LOGGER.warning("Error while receiving. Retrying...");
                    } catch (InterruptedException e) {
                        LOGGER.warning("Listener interrupted");
                    }
                });
                listener.start();
                packet = queue.poll(2, TimeUnit.SECONDS);
                listener.interrupt();
                if (packet == null) {
                    channel.disconnect();
                    channel.bind(null);
                }
            } while (packet == null);
            LOGGER.info("Received %d bytes from %s : %s%n".formatted(
                packet.size(),
                packet.address(),
                packet.message()
            ));
            upperCaseLines.add(packet.message());
        }
        channel.close();

        // Write upperCaseLines to outFilename in UTF-8
        Files.write(Path.of(outFilename), upperCaseLines, UTF8, CREATE, WRITE, TRUNCATE_EXISTING);
    }
}