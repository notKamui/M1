package fr.upem.net.udp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ClientUpperCaseUDPTimeout {
    private static final Logger LOGGER = Logger.getLogger(ClientUpperCaseUDPTimeout.class.getName());

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
                var bytes = cs.encode(line);

                channel.send(bytes, server);
                LOGGER.info("Sent : " + line);
                var queue = new ArrayBlockingQueue<Packet>(1);
                var listener = new Thread(() -> {
                    try {
                        var sender = (InetSocketAddress) channel.receive(buffer);
                        buffer.flip();
                        queue.put(new Packet(sender, buffer.remaining(), cs.decode(buffer).toString()));
                        buffer.clear();
                    } catch (IOException | InterruptedException e) {
                        LOGGER.warning("Error while receiving");
                    }
                });
                listener.start();
                var packet = queue.poll(2, TimeUnit.SECONDS);
                listener.interrupt();
                if (packet == null) {
                    LOGGER.warning("The server did not answer");
                    channel.disconnect();
                    channel.bind(null);
                } else {
                    LOGGER.info("Received %d bytes from %s : %s%n".formatted(
                        packet.size(),
                        packet.address(),
                        packet.message()
                    ));
                }
            }
        }
        channel.close();
    }
}
