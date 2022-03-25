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
import java.util.Scanner;
import java.util.logging.Logger;

public class ServerEchoWithConsoleAndTimeout {
	static private class Context {
		private final SelectionKey key;
		private final SocketChannel sc;
		private final ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
		private boolean closed = false;
		private boolean activeSinceLastCheck = true;

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
		 * @throws java.io.IOException if the read operation fails
		 */
		private void doRead() throws IOException {
			activeSinceLastCheck = true;
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
		 * @throws java.io.IOException if the write operation fails
		 */
		private void doWrite() throws IOException {
			activeSinceLastCheck = true;
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

	static class CommandPipe {
		private final Object lock = new Object();
		private String command;

		public void in(String command) {
			synchronized (lock) {
				this.command = command;
			}
		}

		public String out() {
			synchronized (lock) {
				if (command == null) throw new IllegalStateException("No command");
				var ret = command;
				command = null;
				return ret;
			}
		}

		public boolean isEmpty() {
			synchronized (lock) {
				return command == null;
			}
		}
	}

	private static final int BUFFER_SIZE = 1_024;
	private static final Logger logger = Logger.getLogger(ServerEchoWithConsoleAndTimeout.class.getName());
	private static final int TIMEOUT = 60_000;

	private final ServerSocketChannel serverSocketChannel;
	private final Selector selector;
	private final Thread console;
	private final CommandPipe pipe;
	private long ticks = 0;

	public ServerEchoWithConsoleAndTimeout(int port) throws IOException {
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.bind(new InetSocketAddress(port));
		selector = Selector.open();
		console = new Thread(this::consoleRun);
		pipe = new CommandPipe();
	}

	private void consoleRun() {
		try {
			try (var scanner = new Scanner(System.in)) {
				while (!Thread.interrupted() && scanner.hasNextLine()) {
					var msg = scanner.nextLine();
					sendCommand(msg);
				}
			}
			logger.info("Console thread stopping");
		} catch (InterruptedException e) {
			logger.info("Console thread has been interrupted");
		}
	}

	/**
	 * Send instructions to the selector via a BlockingQueue and wake it up
	 *
	 * @param msg the message to send
	 * @throws InterruptedException if the selector is not ready
	 */
	private void sendCommand(String msg) throws InterruptedException {
		if (!pipe.isEmpty()) return;
		pipe.in(msg);
		selector.wakeup();
	}

	/**
	 * Processes the command from the BlockingQueue
	 */
	private void processCommands() {
		if (pipe.isEmpty()) return;
		var cmd = pipe.out();
		switch (cmd.toUpperCase()) {
			case "INFO" -> {
				var connected = selector.keys().stream()
					.filter(key -> key.channel() != serverSocketChannel)
					.filter(key -> !((Context)key.attachment()).closed)
					.count();
				logger.info("Active clients: " + connected);
			}

			case "SHUTDOWN" -> {
				logger.info("Shutting down softly...");
				try {
					serverSocketChannel.close();
				} catch (IOException e) {
					// Do nothing
				}
			}

			case "SHUTDOWNNOW" -> {
				logger.info("Shutting down now...");
				selector.keys().forEach(this::silentlyClose);
				Thread.currentThread().interrupt();
			}

			default -> logger.warning("Unknown command: " + cmd);
		}
	}

	public void purgeInactive() {
		selector.keys().stream()
			.filter(key -> key.channel() != serverSocketChannel)
			.forEach(key -> {
				var ctx = (Context) key.attachment();
				if (!ctx.activeSinceLastCheck) {
					try {
						logger.info("Closing inactive connection from " + ctx.sc.getRemoteAddress());
						key.channel().close();
					} catch (IOException e) {
						// Do nothing
					}
				} else {
					ctx.activeSinceLastCheck = false;
				}
			});
	}

	public void launch() throws IOException {
		serverSocketChannel.configureBlocking(false);
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

		console.setDaemon(true);
		console.start();

		while (!Thread.interrupted()) {
			Helpers.printKeys(selector); // for debug
			System.out.println("Starting select");
			try {
				var timestamp = System.currentTimeMillis();
				selector.select(this::treatKey, TIMEOUT);
				ticks += System.currentTimeMillis() - timestamp;
				if (ticks > TIMEOUT) {
					purgeInactive();
					ticks %= TIMEOUT;
				}
				processCommands();
			} catch (UncheckedIOException tunneled) {
				throw tunneled.getCause();
			}
			System.out.println("Select finished");
		}

		console.interrupt();
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
		new ServerEchoWithConsoleAndTimeout(Integer.parseInt(args[0])).launch();
	}

	private static void usage() {
		System.out.println("Usage : ServerEcho port");
	}
}