package fr.upem.net.udp;

import java.util.logging.Logger;
import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ServerIdUpperCaseUDP {

    private static final Logger logger = Logger.getLogger(ServerIdUpperCaseUDP.class.getName());
    private static final Charset UTF8 = StandardCharsets.UTF_8;
    private static final int BUFFER_SIZE = 1024;

    private final DatagramChannel dc;
    private final ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);

    public ServerIdUpperCaseUDP(int port) throws IOException {
        dc = DatagramChannel.open();
        dc.bind(new InetSocketAddress(port));
        logger.info("ServerBetterUpperCaseUDP started on port " + port);
    }

    public void serve() throws IOException {
        try {
            while (!Thread.interrupted()) {
                buffer.clear();
                // 1) receive request from client
                var clientAddress = (InetSocketAddress) dc.receive(buffer);
                buffer.flip();
                // 2) read id
                if (buffer.remaining() < Long.BYTES) {
                    logger.warning("Received invalid packet from " + clientAddress);
                    continue;
                }
                var id = buffer.getLong();
                // 3) decode msg in request String upperCaseMsg = msg.toUpperCase();
                var msg = UTF8.decode(buffer).toString();
                logger.info("Received request from " + clientAddress + " : " + id + " " + msg);
                var upperCaseMsg = msg.toUpperCase();
                // 4) create packet with id, upperCaseMsg in UTF-8
                buffer.clear();
                buffer.putLong(id);
                buffer.put(UTF8.encode(upperCaseMsg));
                // 5) send the packet to client
                buffer.flip();
                logger.info("Sending response to " + clientAddress + " : " + id + " " + upperCaseMsg);
                dc.send(buffer, clientAddress);
            }
        } finally {
            dc.close();
        }
    }

    public static void usage() {
        System.out.println("Usage : ServerIdUpperCaseUDP port");
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            usage();
            return;
        }

        var port = Integer.parseInt(args[0]);

        if (!(port >= 1024) & port <= 65535) {
            logger.severe("The port number must be between 1024 and 65535");
            return;
        }

        try {
            new ServerIdUpperCaseUDP(port).serve();
        } catch (BindException e) {
            logger.severe("Server could not bind on " + port + "\nAnother server is probably running on this port.");
            System.exit(1);
        }
    }
}