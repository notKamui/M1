package fr.upem.net.tcp.nonblocking;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerEcho {
	static private class Context {
		private final SelectionKey key;
		private final SocketChannel sc;
		private final ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
		private boolean closed = false;

		private Context(SelectionKey key) {
			this.key = key;
			this.sc = (SocketChannel) key.channel();
		}

		/**
		 * Update the interestOps of the key looking only at values of the boolean
		 * closed and the ByteBuffer buffer.
		 *
		 * The convention is that buff is in write-mode.
		 */
		private void updateInterestOps() {
			if (closed || !buffer.hasRemaining()) {
				key.interestOps(SelectionKey.OP_WRITE);
			} else if (buffer.position() == 0) {
				key.interestOps(SelectionKey.OP_READ);
			} else {
				key.interestOps(SelectionKey.OP_WRITE | SelectionKey.OP_READ);
			}
		}

		/**
		 * Performs the read action on sc
		 *
		 * The convention is that buffer is in write-mode before calling doRead and is in
		 * write-mode after calling doRead
		 *
		 * @throws IOException if the read operation fails
		 */
		private void doRead() throws IOException {
			var n = sc.read(buffer);
			if (n == -1) {
				logger.info("Connection closed by " + sc.getRemoteAddress());
				closed = true;
			}
			updateInterestOps();
		}

		/**
		 * Performs the write action on sc
		 *
		 * The convention is that buffer is in write-mode before calling doWrite and is in
		 * write-mode after calling doWrite
		 *
		 * @throws IOException if the write operation fails
		 */
		private void doWrite() throws IOException {
			buffer.flip();
			if (closed && !buffer.hasRemaining()) {
				silentlyClose();
				return;
			}
			sc.write(buffer);
			buffer.compact();
			updateInterestOps();
		}

		private void silentlyClose() {
			try {
				sc.close();
			} catch (IOException e) {
				// ignore exception
			}
		}
	}

	private static final int BUFFER_SIZE = 1_024;
	private static final Logger logger = Logger.getLogger(ServerEcho.class.getName());

	private final ServerSocketChannel serverSocketChannel;
	private final Selector selector;

	public ServerEcho(int port) throws IOException {
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
				throw tunneled.getCause();
			}
			System.out.println("Select finished");
		}
	}

	private void treatKey(SelectionKey key) {
		Helpers.printSelectedKey(key); // for debug
		try {
			if (key.isValid() && key.isAcceptable()) {
				doAccept(key);
			}
		} catch (IOException ioe) {
			// lambda call in select requires to tunnel IOException
			throw new UncheckedIOException(ioe);
		}
		try {
			if (key.isValid() && key.isWritable()) {
				((Context) key.attachment()).doWrite();
			}
			if (key.isValid() && key.isReadable()) {
				((Context) key.attachment()).doRead();
			}
		} catch (IOException e) {
			logger.info("Connection closed with client due to IOException");
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
		var skey = client.register(selector, SelectionKey.OP_READ);
		skey.attach(new Context(skey));
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
		new ServerEcho(Integer.parseInt(args[0])).launch();
	}

	private static void usage() {
		System.out.println("Usage : ServerEcho port");
	}
}