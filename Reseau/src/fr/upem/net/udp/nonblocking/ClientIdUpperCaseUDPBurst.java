package fr.upem.net.udp.nonblocking;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

public class ClientIdUpperCaseUDPBurst {

    private static final Logger logger = Logger.getLogger(ClientIdUpperCaseUDPBurst.class.getName());
    private static final Charset UTF8 = StandardCharsets.UTF_8;
    private static final int BUFFER_SIZE = 1024;

    private enum State {
        SENDING, RECEIVING, FINISHED
    }

    private final List<String> lines;
    private final String[] upperCaseLines;
    private final BitSet answerLog;
    private final long timeout;
    private final InetSocketAddress serverAddress;
    private final DatagramChannel dc;
    private final Selector selector;
    private final SelectionKey uniqueKey;

    private final ByteBuffer sendBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
    private final ByteBuffer receiveBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);

    private long currentRequestStartTime;
    private long currentId = 0;

    private State state;

    private static void usage() {
        System.out.println("Usage : ClientIdUpperCaseUDPOneByOne in-filename out-filename timeout host port ");
    }

    private ClientIdUpperCaseUDPBurst(
        List<String> lines,
        long timeout,
        InetSocketAddress serverAddress,
        DatagramChannel dc,
        Selector selector,
        SelectionKey uniqueKey
    ) {
        this.lines = lines;
        this.timeout = timeout;
        this.serverAddress = serverAddress;
        this.dc = dc;
        this.selector = selector;
        this.uniqueKey = uniqueKey;
        this.state = State.SENDING;

        this.upperCaseLines = new String[lines.size()];
        this.answerLog = new BitSet(lines.size());
        this.answerLog.flip(0, lines.size());
    }

    public static ClientIdUpperCaseUDPBurst create(
        String inFilename,
        long timeout,
        InetSocketAddress serverAddress
    ) throws IOException {
        Objects.requireNonNull(inFilename);
        Objects.requireNonNull(serverAddress);
        Objects.checkIndex(timeout, Long.MAX_VALUE);

        // Read all lines of inFilename opened in UTF-8
        var lines = Files.readAllLines(Path.of(inFilename), UTF8);
        var dc = DatagramChannel.open();
        dc.configureBlocking(false);
        dc.bind(null);
        var selector = Selector.open();
        var uniqueKey = dc.register(selector, SelectionKey.OP_WRITE);
        return new ClientIdUpperCaseUDPBurst(lines, timeout, serverAddress, dc, selector, uniqueKey);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length != 5) {
            usage();
            return;
        }

        var inFilename = args[0];
        var outFilename = args[1];
        var timeout = Long.parseLong(args[2]);
        var server = new InetSocketAddress(args[3], Integer.parseInt(args[4]));

        // Create client with the parameters and launch it
        var upperCaseLines = create(inFilename, timeout, server).launch();

        Files.write(Path.of(outFilename), upperCaseLines, UTF8, CREATE, WRITE, TRUNCATE_EXISTING);
    }

    private List<String> launch() throws IOException {
        try {
            while (!isFinished()) {
                try {
                    selector.select(this::treatKey, updateInterestOps());
                } catch (UncheckedIOException tunneled) {
                    throw tunneled.getCause();
                }
            }
            return List.of(upperCaseLines);
        } finally {
            dc.close();
        }
    }

    private void treatKey(SelectionKey key) {
        try {
            if (key.isValid() && key.isWritable()) {
                doWrite();
            }
            if (key.isValid() && key.isReadable()) {
                doRead();
            }
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    /**
     * Updates the interestOps on key based on state of the context
     *
     * @return the timeout for the next select (0 means no timeout)
     */

    private long updateInterestOps() {
        switch (state) {
            case RECEIVING:
                var timeout = this.timeout - (System.currentTimeMillis() - currentRequestStartTime);
                if (timeout > 0) {
                    uniqueKey.interestOps(SelectionKey.OP_READ);
                    return timeout;
                }
                state = State.SENDING;
                currentId = 0;
            case SENDING:
                uniqueKey.interestOps(SelectionKey.OP_WRITE);
                return 0;
            default:
                throw new AssertionError("Unexpected state: " + state);
        }
    }

    private boolean isFinished() {
        return state == State.FINISHED;
    }

    /**
     * Performs the receptions of packets
     *
     * @throws java.io.IOException if an I/O error occurs
     */
    private void doRead() throws IOException {
        if (dc.receive(receiveBuffer) == null) {
            logger.warning("Selector lied to us, we received a null packet");
            return;
        }
        receiveBuffer.flip();
        if (receiveBuffer.remaining() < Long.BYTES) {
            logger.warning("Received invalid packet");
            return;
        }

        var id = receiveBuffer.getLong();
        if (!answerLog.get((int) id)) return;
        var line = UTF8.decode(receiveBuffer).toString();
        logger.info("Received packet with id " + id + " : " + line);
        upperCaseLines[(int) id] = line;
        answerLog.flip((int) id);
        receiveBuffer.clear();

        if (answerLog.cardinality() == 0) {
            state = State.FINISHED;
        }
    }

    /**
     * Tries to send the packets
     *
     * @throws java.io.IOException if an I/O error occurs
     */
    private void doWrite() throws IOException {
        if (answerLog.get((int) currentId))  {
            fillSendBuffer(currentId);
            dc.send(sendBuffer, serverAddress);
            if (sendBuffer.hasRemaining()) {
                logger.warning("Selector lied to us: buffer.hasRemaining()");
                return;
            }
            logger.info("Sent packet with id " + currentId);
            sendBuffer.flip();
        }
        currentId++;
        if (currentId >= lines.size()) {
            receiveBuffer.clear();
            state = State.RECEIVING;
            currentRequestStartTime = System.currentTimeMillis();
        }
    }

    private void fillSendBuffer(long id) {
        sendBuffer.clear();
        sendBuffer.putLong(id);
        sendBuffer.put(UTF8.encode(lines.get((int) id)));
        sendBuffer.flip();
    }
}
