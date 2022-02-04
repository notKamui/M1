package fr.upem.net.udp;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ClientIdUpperCaseUDPBurst {

    private static final Charset UTF8 = StandardCharsets.UTF_8;
    private static final int BUFFER_SIZE = 1024;
    private static final Logger LOGGER = Logger.getLogger(ClientIdUpperCaseUDPBurst.class.getName());
    private final List<String> lines;
    private final int nbLines;
    private final String[] upperCaseLines;
    private final int timeout;
    private final String outFilename;
    private final InetSocketAddress serverAddress;
    private final DatagramChannel dc;
    private final AnswersLog answersLog;

    public ClientIdUpperCaseUDPBurst(List<String> lines, int timeout, InetSocketAddress serverAddress, String outFilename) throws IOException {
        this.lines = lines;
        this.nbLines = lines.size();
        this.timeout = timeout;
        this.outFilename = outFilename;
        this.serverAddress = serverAddress;
        this.dc = DatagramChannel.open();
        dc.bind(null);
        this.upperCaseLines = new String[nbLines];
        this.answersLog = null; // TODO
    }

    public static void usage() {
        System.out.println("Usage : ClientIdUpperCaseUDPBurst in-filename out-filename timeout host port ");
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length != 5) {
            usage();
            return;
        }

        String inFilename = args[0];
        String outFilename = args[1];
        int timeout = Integer.parseInt(args[2]);
        String host = args[3];
        int port = Integer.parseInt(args[4]);
        InetSocketAddress serverAddress = new InetSocketAddress(host, port);

        //Read all lines of inFilename opened in UTF-8
        List<String> lines = Files.readAllLines(Paths.get(inFilename), UTF8);
        //Create client with the parameters and launch it
        ClientIdUpperCaseUDPBurst client = new ClientIdUpperCaseUDPBurst(lines, timeout, serverAddress, outFilename);
        client.launch();

    }

    private void senderThreadRun() {
        while (true) {
            answersLog.forEach(line -> {
                try {
                    dc.send(UTF8.encode(line), serverAddress);
                } catch (IOException e) {
                    LOGGER.severe("Error while sending messages");
                }
            });
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    public void launch() throws IOException {
        var bitset = new BitSet(nbLines);
        Thread senderThread = new Thread(this::senderThreadRun);
        var executor = Executors
            .newSingleThreadScheduledExecutor()
            .scheduleAtFixedRate(this::senderThreadRun, 0, timeout, TimeUnit.MILLISECONDS);
        senderThread.start();


        senderThread.interrupt();
        Files.write(Paths.get(outFilename), Arrays.asList(upperCaseLines), UTF8,
            StandardOpenOption.CREATE,
            StandardOpenOption.WRITE,
            StandardOpenOption.TRUNCATE_EXISTING);

    }

    private static class AnswersLog {
        private final Map<Integer, String> messages;

        public AnswersLog(List<String> messages) {
            this.messages = IntStream.range(0, messages.size())
                .boxed()
                .collect(Collectors.toMap(i -> i, messages::get));
        }

        public void remove(int id) {
            synchronized (messages) {
                messages.remove(id);
            }
        }

        public void forEach(Consumer<? super String> consumer) {
            synchronized (messages) {
                messages.values().forEach(consumer);
            }
        }
    }
}


