package fr.upem.net.udp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ClientUpperCaseUDPRetry {
    private static final Logger LOGGER = Logger.getLogger(ClientUpperCaseUDPRetry.class.getName());

    public static final int BUFFER_SIZE = 1024;

    private static void usage() {
        System.out.println("Usage : NetcatUDP host port charset");
    }

    private record Packet(InetSocketAddress address, int size, String message) {
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length != 3) {
            usage();
            return;
        }

        var server = new InetSocketAddress(args[0], Integer.parseInt(args[1]));
        var cs = Charset.forName(args[2]);
        var buffer = ByteBuffer.allocate(BUFFER_SIZE);

        var channel = DatagramChannel.open();
        channel.bind(null);
        try (var scanner = new Scanner(System.in)) {
            while (scanner.hasNextLine()) {
                var line = scanner.nextLine();
                LOGGER.info("Sending : " + line);
                var queue = new ArrayBlockingQueue<Packet>(1);
                Packet packet;
                do {
                    var bytes = cs.encode(line);
                    channel.send(bytes, server);
                    var finalChannel = channel;
                    var listener = new Thread(() -> {
                        try {
                            var sender = (InetSocketAddress) finalChannel.receive(buffer);
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
                        channel = DatagramChannel.open();
                        channel.bind(null);
                    }
                } while (packet == null);
                LOGGER.info("Received %d bytes from %s : %s%n".formatted(
                    packet.size(),
                    packet.address(),
                    packet.message()
                ));
            }
            channel.close();
        }
    }
}
