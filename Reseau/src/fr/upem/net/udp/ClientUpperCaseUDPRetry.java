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
    public static final int BUFFER_SIZE = 1024;
    private static final Logger LOGGER = Logger.getLogger(ClientUpperCaseUDPRetry.class.getName());

    private static void usage() {
        System.out.println("Usage : NetcatUDP host port charset");
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            usage();
            return;
        }

        var server = new InetSocketAddress(args[0], Integer.parseInt(args[1]));
        var cs = Charset.forName(args[2]);

        var queue = new ArrayBlockingQueue<Packet>(1);

        try (var channel = DatagramChannel.open()) {
            channel.bind(null);
            var thread = new Thread(() -> {
                while (!Thread.interrupted()) {
                    var buffer = ByteBuffer.allocate(BUFFER_SIZE);
                    try {
                        var sender = (InetSocketAddress) channel.receive(buffer);
                        buffer.flip();
                        queue.put(new Packet(sender, buffer.remaining(), cs.decode(buffer).toString()));
                    } catch (AsynchronousCloseException | InterruptedException e) {
                        return;
                    } catch (IOException e) {
                        LOGGER.severe(e.getMessage());
                    }
                    buffer.clear();
                }
            });
            thread.start();
            try (var scanner = new Scanner(System.in)) {
                while (scanner.hasNextLine()) {
                    try {
                        var line = scanner.nextLine();
                        Packet packet;
                        do {
                            LOGGER.info("Sending: " + line);
                            channel.send(cs.encode(line), server);
                            packet = queue.poll(2, TimeUnit.SECONDS);
                        } while (packet == null);
                        LOGGER.info("Received %d bytes from %s : %s%n".formatted(
                            packet.size(),
                            packet.address(),
                            packet.message()
                        ));
                    } catch (AsynchronousCloseException | InterruptedException e) {
                        LOGGER.warning("Error while sending message");
                    } catch (IOException e) {
                        LOGGER.severe(e.getMessage());
                    }
                }
            }
            thread.interrupt();
        }
    }

    private record Packet(InetSocketAddress address, int size, String message) {
    }
}
