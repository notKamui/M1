package fr.upem.net.udp.nonblocking;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.logging.Logger;

public class ServerEchoMultiPort {
    private static final Logger logger = Logger.getLogger(ServerEchoMultiPort.class.getName());
    private static final int BUFFER_SIZE = 1024;

    private final Selector selector;
    private final int firstPort;
    private final int lastPort;

    private static class Context {
        private InetSocketAddress sender;
        private final ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
    }

    public ServerEchoMultiPort(int firstPort, int lastPort) throws IOException {
        this.firstPort = firstPort;
        this.lastPort = lastPort;
        selector = Selector.open();
        for (int port = firstPort; port <= lastPort; port++) {
            DatagramChannel dc = DatagramChannel.open();
            dc.bind(new InetSocketAddress(port));
            dc.configureBlocking(false);
            dc.register(selector, SelectionKey.OP_READ, new Context());
        }
    }

    public void serve() throws IOException {
        logger.info("ServerEcho started on ports " + firstPort + " to " + lastPort);
        while (!Thread.interrupted()) {
            try {
                selector.select(this::treatKey);
            } catch (UncheckedIOException e) {
                logger.severe("Error while selecting ; Your network card may have fried");
                return;
            }
        }
    }

    private void treatKey(SelectionKey key) {
        try {
            if (key.isValid() && key.isWritable()) {
                doWrite(key);
            }
            if (key.isValid() && key.isReadable()) {
                doRead(key);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

    }

    private void doRead(SelectionKey key) throws IOException {
        var context = (Context) key.attachment();
        var buffer = context.buffer;
        var dc = (DatagramChannel) key.channel();
        buffer.clear();
        context.sender = (InetSocketAddress) dc.receive(buffer);
        if (context.sender == null) return;
        logger.info("Received from " + context.sender);
        buffer.flip();
        key.interestOps(SelectionKey.OP_WRITE);
    }

    private void doWrite(SelectionKey key) throws IOException {
        var context = (Context) key.attachment();
        var buffer = context.buffer;
        var dc = (DatagramChannel) key.channel();
        dc.send(buffer, context.sender);
        if (buffer.hasRemaining()) return;
        logger.info("Sent back to " + context.sender);
        context.sender = null;
        key.interestOps(SelectionKey.OP_READ);
    }

    public static void usage() {
        System.out.println("Usage : ServerEcho first_port last_port");
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            usage();
            return;
        }
        new ServerEchoMultiPort(Integer.parseInt(args[0]), Integer.parseInt(args[1])).serve();
    }
}
