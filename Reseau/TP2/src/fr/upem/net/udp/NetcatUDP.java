package fr.upem.net.udp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

public class NetcatUDP {
    public static final int BUFFER_SIZE = 1024;

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
        var buffer = ByteBuffer.allocate(BUFFER_SIZE);

        try (var channel = DatagramChannel.open()) {
            try (var scanner = new Scanner(System.in)) {
                while (scanner.hasNextLine()) {
                    var line = scanner.nextLine();
                    var bytes = cs.encode(line);

                    channel.bind(null);
                    channel.send(bytes, server);
                    System.out.println("Sent : " + line);
                    var sender = (InetSocketAddress) channel.receive(buffer);
                    buffer.flip();
                    System.out.printf("Received %d bytes from %s : %s%n", buffer.remaining(), sender, cs.decode(buffer));
                    buffer.clear();
                }
            }
        }
    }
}
