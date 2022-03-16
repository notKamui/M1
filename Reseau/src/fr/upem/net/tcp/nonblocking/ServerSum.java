package fr.upem.net.tcp.nonblocking;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerSum {

	private static final int BUFFER_SIZE = 2 * Integer.BYTES;
	private static final Logger logger = Logger.getLogger(ServerSum.class.getName());

	private final ServerSocketChannel serverSocketChannel;
	private final Selector selector;

	public ServerSum(int port) throws IOException {
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.bind(new InetSocketAddress(port));
		selector = Selector.open();
	}

	public void launch() throws IOException {
		serverSocketChannel.configureBlocking(false);
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		while (!Thread.interrupted()) {
			Helpers.printKeys(selector); // for debug
			System.out.println("Starting select");
			try {
				selector.select(this::treatKey);
			} catch (UncheckedIOException tunneled) {
				logger.log(Level.SEVERE, "your network card is frying", tunneled);
				return;
			}
			System.out.println("Select finished");
		}
	}

	private void treatKey(SelectionKey key) {
		Helpers.printSelectedKey(key); // for debug
		if (key.isValid() && key.isAcceptable()) {
			try {
				doAccept(key);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
		try {
			if (key.isValid() && key.isWritable()) {
				doWrite(key);
			}
			if (key.isValid() && key.isReadable()) {
				doRead(key);
			}
		} catch (IOException e) {
			silentlyClose(key);
		}
	}

	private void doAccept(SelectionKey key) throws IOException {
		var ssc = (ServerSocketChannel) key.channel();
		var client = ssc.accept();
		if (client == null) {
			logger.warning("accept() returned null");
			return;
		}
		client.configureBlocking(false);
		client.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(BUFFER_SIZE));
	}

	private void doRead(SelectionKey key) throws IOException {
		var client = (SocketChannel) key.channel();
		var buffer = (ByteBuffer) key.attachment();
		if (client.read(buffer) == -1) {
			logger.warning("client closed connection");
			silentlyClose(key);
			return;
		}
		if (buffer.hasRemaining()) return;
		buffer.flip();
		var sum = buffer.getInt() + buffer.getInt();
		buffer.clear();
		buffer.putInt(sum);
		key.interestOps(SelectionKey.OP_WRITE);
	}

	private void doWrite(SelectionKey key) throws IOException {
		var client = (SocketChannel) key.channel();
		var buffer = (ByteBuffer) key.attachment();
		buffer.flip();
		client.write(buffer);
		if (buffer.hasRemaining()) {
			buffer.compact();
			return;
		}
		buffer.clear();
		key.interestOps(SelectionKey.OP_READ);
	}

	private void silentlyClose(SelectionKey key) {
		var sc = (Channel) key.channel();
		try {
			sc.close();
		} catch (IOException e) {
			// ignore exception
		}
	}

	public static void main(String[] args) throws NumberFormatException, IOException {
		if (args.length != 1) {
			usage();
			return;
		}
		new ServerSum(Integer.parseInt(args[0])).launch();
	}

	private static void usage() {
		System.out.println("Usage : ServerSumOneShot port");
	}
}
