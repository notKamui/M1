package fr.upem.net.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.logging.Logger;

public class ClientLongSum {

    public static final Logger logger = Logger.getLogger(ClientLongSum.class.getName());

    private static List<Long> randomLongList(int size) {
        return new Random().longs(size).boxed().toList();
    }

    private static boolean checkSum(List<Long> list, long response) {
        return list.stream().reduce(Long::sum).orElse(0L) == response;
    }

    private static void sendRequest(SocketChannel sc, List<Long> list) throws IOException {
        var buffer = ByteBuffer.allocate(Integer.BYTES + Long.BYTES * list.size());
        buffer.putInt(list.size());
        list.forEach(buffer::putLong);
        buffer.flip();
        sc.write(buffer);
    }

    /**
     * Write all the longs in list in BigEndian on the server and read the long sent
     * by the server and returns it
     *
     * returns Optional.empty if the protocol is not followed by the server but no
     * IOException is thrown
     *
     * @param sc the socket channel to write and read
     * @param list the list of longs to write
     * @return the long read from the server
     * @throws IOException if an IO error occurs
     */
    private static Optional<Long> requestSumForList(SocketChannel sc, List<Long> list) throws IOException {
        sendRequest(sc, list);

        var buffer = ByteBuffer.allocate(Long.BYTES);
        if (!ClientEOS.readFully(sc, buffer)) return Optional.empty();
        buffer.flip();
        return Optional.of(buffer.getLong());
    }

    public static void main(String[] args) throws IOException {
        var server = new InetSocketAddress(args[0], Integer.parseInt(args[1]));
        try (var sc = SocketChannel.open(server)) {
            for (var i = 0; i < 5; i++) {
                var list = randomLongList(50);

                var sum = requestSumForList(sc, list);
                if (sum.isEmpty()) {
                    logger.warning("Connection with server lost.");
                    return;
                }
                if (!checkSum(list, sum.get())) {
                    logger.warning("Oops! Something wrong happened!");
                }
            }
            logger.info("Everything seems ok");
        }
    }
}