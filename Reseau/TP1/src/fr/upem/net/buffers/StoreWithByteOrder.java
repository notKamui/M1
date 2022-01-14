package fr.upem.net.buffers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.Scanner;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

public class StoreWithByteOrder {

	public static void usage() {
		System.out.println("StoreWithByteOrder [LE|BE] filename");
	}

	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			usage();
			return;
		}
		var path = Path.of(args[1]);
		var order = switch (args[0].toUpperCase()) {
			case "LE" -> ByteOrder.LITTLE_ENDIAN;
			case "BE" -> ByteOrder.BIG_ENDIAN;
			default -> {
				System.out.println("Unrecognized option : " + args[0]);
				usage();
				yield null;
			}
		};
		if (order == null) return;

		var bb = ByteBuffer.allocate(8);
		bb.order(order);
		try (
			var outChannel = FileChannel.open(path, WRITE, CREATE, TRUNCATE_EXISTING);
			var scanner = new Scanner(System.in)
		) {
			while (scanner.hasNextLong()) {
				var l = scanner.nextLong();
				bb.putLong(l);
				bb.flip();
				outChannel.write(bb);
				bb.clear();
			}
		}
	}
}