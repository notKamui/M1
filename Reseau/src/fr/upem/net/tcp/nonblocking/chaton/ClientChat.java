package fr.upem.net.tcp.nonblocking.chaton;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Scanner;
import java.util.logging.Logger;

public class ClientChat {
 
    static private class Context {
        private final SelectionKey key;
        private final SocketChannel sc;
        private final MessageReader reader = new MessageReader();
        private final ByteBuffer bufferIn = ByteBuffer.allocate(BUFFER_SIZE);
        private final ByteBuffer bufferOut = ByteBuffer.allocate(BUFFER_SIZE);
        private final ArrayDeque<ByteBuffer> queue = new ArrayDeque<>();
        private boolean closed = false;

        private Context(SelectionKey key) {
            this.key = key;
            this.sc = (SocketChannel) key.channel();
        }

        /**
         * Process the content of bufferIn
         *
         * The convention is that bufferIn is in write-mode before the call to process
         * and after the call
         *
         */
        private void processIn() {
            while (bufferIn.hasRemaining()) {
                switch (reader.process(bufferIn)) {
                    case ERROR:
                        silentlyClose();
                    case REFILL:
                        return;
                    case DONE:
                        log(reader.get());
                        reader.reset();
                        break;
                }
            }
        }

        /**
         * Add a message to the message queue, tries to fill bufferOut and updateInterestOps
         *
         * @param msg the message to add
         */
        private void queueMessage(Message msg) {
            var username = CHARSET.encode(msg.username());
            var text = CHARSET.encode(msg.text());
            var buffer = ByteBuffer.allocate(username.remaining() + text.remaining() + Integer.BYTES * 2);
            buffer.putInt(username.remaining())
                .put(username)
                .putInt(text.remaining())
                .put(text)
                .flip();
            queue.offer(buffer);
            processOut();
            updateInterestOps();
        }

        /**
         * Try to fill bufferOut from the message queue
         *
         */
        private void processOut() {
            while (!queue.isEmpty() && bufferOut.hasRemaining()) {
                var msg = queue.peek();
                if (!msg.hasRemaining()) {
                    queue.poll();
                    continue;
                }
                if (msg.remaining() <= bufferOut.remaining()) {
                    bufferOut.put(msg);
                } else {
                    var oldLimit = msg.limit();
                    msg.limit(bufferOut.remaining());
                    bufferOut.put(msg);
                    msg.limit(oldLimit);
                }
            }
        }

        /**
         * Update the interestOps of the key looking only at values of the boolean
         * closed and of both ByteBuffers.
         *
         * The convention is that both buffers are in write-mode before the call to
         * updateInterestOps and after the call. Also it is assumed that process has
         * been be called just before updateInterestOps.
         */

        private void updateInterestOps() {
            int interestOps = 0;
            if (!closed && bufferIn.hasRemaining()) {
                interestOps |= SelectionKey.OP_READ;
            }
            if (bufferOut.position() != 0) {
                interestOps |= SelectionKey.OP_WRITE;
            }

            if (interestOps == 0) {
                silentlyClose();
                return;
            }
            key.interestOps(interestOps);
        }

        private void silentlyClose() {
            try {
                sc.close();
            } catch (IOException e) {
                // ignore exception
            }
        }

        /**
         * Performs the read action on sc
         *
         * The convention is that both buffers are in write-mode before the call to
         * doRead and after the call
         *
         * @throws IOException if an I/O error occurs
         */
        private void doRead() throws IOException {
            if (sc.read(bufferIn) == -1) {
                logger.info("Connection closed by " + sc.getRemoteAddress());
                closed = true;
            }
            processIn();
            updateInterestOps();
        }

        /**
         * Performs the write action on sc
         *
         * The convention is that both buffers are in write-mode before the call to
         * doWrite and after the call
         *
         * @throws IOException if an I/O error occurs
         */

        private void doWrite() throws IOException {
            bufferOut.flip();
            sc.write(bufferOut);
            bufferOut.compact();
            processOut();
            updateInterestOps();
        }

        public void doConnect() throws IOException {
            if (!sc.finishConnect()) {
                logger.warning("Selector lied");
                return;
            }
            key.interestOps(SelectionKey.OP_READ);
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

    private final static int BUFFER_SIZE = 10_000;
    private final static Logger logger = Logger.getLogger(ClientChat.class.getName());
    private final static Charset CHARSET = StandardCharsets.UTF_8;

    private final SocketChannel sc;
    private final Selector selector;
    private final InetSocketAddress serverAddress;
    private final String login;
    private final Thread console;
    private Context uniqueContext;
    private final CommandPipe pipe;
    private boolean quitting = false;

    public ClientChat(String login, InetSocketAddress serverAddress) throws IOException {
        this.serverAddress = serverAddress;
        this.login = login;
        this.sc = SocketChannel.open();
        this.selector = Selector.open();
        this.console = new Thread(this::consoleRun);
        this.pipe = new CommandPipe();
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
        if (cmd.equals("/quit")) {
            logger.info("Quitting");
            try {
                sc.close();
            } catch (IOException e) {
                // do nothing
            }
            quitting = true;
            return;
        }
        uniqueContext.queueMessage(new Message(login, cmd));
    }

    public void launch() throws IOException {
        sc.configureBlocking(false);
        var key = sc.register(selector, SelectionKey.OP_CONNECT);
        uniqueContext = new Context(key);
        key.attach(uniqueContext);
        sc.connect(serverAddress);

        console.setDaemon(true);
        console.start();

        while (!Thread.interrupted()) {
            try {
                selector.select(this::treatKey);
                processCommands();
                if (quitting) {
                    console.interrupt();
                    break;
                }
            } catch (UncheckedIOException tunneled) {
                silentlyClose(key);
                throw tunneled.getCause();
            }
        }
    }

    private void treatKey(SelectionKey key) {
        try {
            if (key.isValid() && key.isConnectable()) {
                uniqueContext.doConnect();
            }
            if (key.isValid() && key.isWritable()) {
                uniqueContext.doWrite();
            }
            if (key.isValid() && key.isReadable()) {
                uniqueContext.doRead();
            }
        } catch (IOException ioe) {
            // lambda call in select requires to tunnel IOException
            throw new UncheckedIOException(ioe);
        }
    }

    public static void log(Message msg) {
        System.out.println(msg.username() + ": " + msg.text());
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
        if (args.length != 3) {
            usage();
            return;
        }
        new ClientChat(args[0], new InetSocketAddress(args[1], Integer.parseInt(args[2]))).launch();
    }

    private static void usage() {
        System.out.println("Usage : ClientChat login hostname port");
    }
}