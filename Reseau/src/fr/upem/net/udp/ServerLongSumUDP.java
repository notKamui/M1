package fr.upem.net.udp;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ServerLongSumUDP {

    private static final Logger logger = Logger.getLogger(ServerLongSumUDP.class.getName());
    private static final int BUFFER_SIZE = 1024;

    private final DatagramChannel dc;
    private final ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);

    private static class SessionManager {
        private final Map<InetSocketAddress, Map<Long, Session>> addressToSessionById = new HashMap<>();

        // opens a new session if not already opened and returns it
        Session getOrOpenSession(InetSocketAddress address, long id, long totalOper) {
            var sessions = addressToSessionById.computeIfAbsent(address, k -> new HashMap<>());
            return sessions.computeIfAbsent(id, k -> new Session(totalOper));
        }

        // adds a new operand to a session and returns false if the session isn't completed
        boolean tryAdd(InetSocketAddress address, long sessionId, long idPosOper, long totalOper, long opValue) {
            var session = getOrOpenSession(address, sessionId, totalOper);
            session.tryAdd(idPosOper, opValue);
            return session.isComplete();
        }

        // returns the result of the session or 0 if the session doesn't exist
        long sumOfSession(InetSocketAddress address, long sessionId) {
            var sessions = addressToSessionById.get(address);
            if (sessions == null) return 0L;
            var session = sessions.get(sessionId);
            if (session == null) return 0L;
            return session.sum();
        }
    }

    private static class Session {
        private final BitSet status;
        private long sum;

        Session(long totalOper) {
            this.status = new BitSet((int) totalOper);
            this.status.flip(0, (int) totalOper);
            this.sum = 0;
        }

        // tries to add to the sum and updates the status.
        void tryAdd(long idPosOper, long opValue) {
            if (!status.get((int) idPosOper)) return;
            sum += opValue;
            status.set((int) idPosOper, false);
        }

        // returns true if the session was completed.
        boolean isComplete() {
            return status.cardinality() == 0;
        }

        long sum() {
            return sum;
        }
    }

    public ServerLongSumUDP(int port) throws IOException {
        dc = DatagramChannel.open();
        dc.bind(new InetSocketAddress(port));
        logger.info("ServerBetterUpperCaseUDP started on port " + port);
    }

    void sendAck(InetSocketAddress address, long sessionId, long idPosOper) throws IOException {
        buffer.clear();
        buffer.put((byte)2);
        buffer.putLong(sessionId);
        buffer.putLong(idPosOper);
        buffer.flip();
        dc.send(buffer, address);
    }

    void sendResult(InetSocketAddress address, long sessionId, long result) throws IOException {
        buffer.clear();
        buffer.put((byte)3);
        buffer.putLong(sessionId);
        buffer.putLong(result);
        buffer.flip();
        dc.send(buffer, address);
    }

    public void serve() throws IOException {
        try {
            var sessionManager = new SessionManager();
            while (!Thread.interrupted()) {
                buffer.clear();
                var address = (InetSocketAddress) dc.receive(buffer);
                buffer.flip();
                var opCode = buffer.get();
                if (opCode != 1) {
                    logger.warning("Unsupported opCode " + opCode);
                    continue;
                }
                var sessionId = buffer.getLong();
                var idPosOper = buffer.getLong();
                var totalOper = buffer.getLong();
                var opValue = buffer.getLong();
                logger.info("Received " + opCode + " " + sessionId + " " + idPosOper + " " + totalOper + " " + opValue);
                logger.info("Sending ACK");
                sendAck(address, sessionId, idPosOper);
                if (sessionManager.tryAdd(address, sessionId, idPosOper, totalOper, opValue)) {
                    logger.info("Session " + sessionId + " completed, sending RES");
                    var sum = sessionManager.sumOfSession(address, sessionId);
                    sendResult(address, sessionId, sum);
                }
            }
        } finally {
            dc.close();
        }
    }

    public static void usage() {
        System.out.println("Usage : ServerLongSumUDP port");
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            usage();
            return;
        }

        var port = Integer.parseInt(args[0]);

        if (!(port >= 1024) & port <= 65535) {
            logger.severe("The port number must be between 1024 and 65535");
            return;
        }

        try {
            new ServerLongSumUDP(port).serve();
        } catch (BindException e) {
            logger.severe("Server could not bind on " + port + "\nAnother server is probably running on this port.");
            System.exit(1);
        }
    }
}