package fr.upem.net.buffers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.charset.Charset;

public class ReadStandardInputWithEncoding {

    private static final int BUFFER_SIZE = 1024;

    private static void usage() {
        System.out.println("Usage: ReadStandardInputWithEncoding charset");
    }

    private static String stringFromStandardInput(Charset cs) throws IOException {
        try (var channel = Channels.newChannel(System.in)) {
            var bb = ByteBuffer.allocate(BUFFER_SIZE);
            while (channel.read(bb) != -1) {
                if (!bb.hasRemaining()) {
                    var newbb = ByteBuffer.allocate(bb.capacity() * 2);
                    bb.flip();
                    newbb.put(bb);
                    bb = newbb;
                }
            }
            bb.flip();
            return cs.decode(bb).toString();
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            usage();
            return;
        }
        Charset cs = Charset.forName(args[0]);
        System.out.print(stringFromStandardInput(cs));
    }
}
