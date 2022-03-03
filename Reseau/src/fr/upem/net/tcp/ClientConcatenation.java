package fr.upem.net.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Logger;

public class ClientConcatenation {

    public static final Logger logger = Logger.getLogger(ClientConcatenation.class.getName());
    public static final Charset CS = StandardCharsets.UTF_8;

    private static List<Long> randomLongList(int size) {
        return new Random().longs(size).boxed().toList();
    }

    private static boolean checkSum(List<Long> list, long response) {
        return list.stream().reduce(Long::sum).orElse(0L) == response;
    }

    private static void sendRequest(SocketChannel sc, List<String> list) throws IOException {
        var encodedStrings = list.stream().map(CS::encode).toArray(ByteBuffer[]::new);
        var sSize = Arrays.stream(encodedStrings).mapToInt(ByteBuffer::remaining).sum();
        var buffer = ByteBuffer.allocate(Integer.BYTES + Long.BYTES * encodedStrings.length + sSize);
        buffer.putInt(encodedStrings.length);
        Arrays.stream(encodedStrings).forEach(s -> buffer.putInt(s.remaining()).put(s));
        buffer.flip();
        sc.write(buffer);
    }

    private static Optional<String> requestConcatenation(SocketChannel sc, List<String> list) throws IOException {
        sendRequest(sc, list);

        var buffer = ByteBuffer.allocate(Integer.BYTES);
        if (!ClientEOS.readFully(sc, buffer)) return Optional.empty();
        buffer.flip();
        var size = buffer.getInt();
        if (size < 0) return Optional.empty();

        buffer = ByteBuffer.allocate(size);
        if (!ClientEOS.readFully(sc, buffer)) return Optional.empty();
        buffer.flip();
        return Optional.of(CS.decode(buffer).toString());
    }

    public static void main(String[] args) throws IOException {
        var server = new InetSocketAddress(args[0], Integer.parseInt(args[1]));
        try (var sc = SocketChannel.open(server)) {
            try (var scanner = new Scanner(System.in)) {
                while (true) {
                    var lines = new ArrayList<String>();
                    String line;
                    do {
                        line = scanner.nextLine().trim();
                        lines.add(line);
                    } while (!line.equals(""));
                    lines.remove(lines.size() - 1);
                    var response = requestConcatenation(sc, lines);
                    if (response.isEmpty()) {
                        logger.warning("Server closed connection");
                        break;
                    }
                    logger.info("Response: " + response.get());
                }
            }
        }
    }
}