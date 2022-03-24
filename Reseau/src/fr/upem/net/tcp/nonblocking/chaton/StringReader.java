package fr.upem.net.tcp.nonblocking.chaton;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static fr.upem.net.tcp.nonblocking.chaton.Reader.ProcessStatus.DONE;
import static fr.upem.net.tcp.nonblocking.chaton.Reader.ProcessStatus.REFILL;

public class StringReader implements Reader<String> {

    private final static int BUFFER_SIZE = 1024;
    private final static Charset CHARSET = StandardCharsets.UTF_8;

    private enum State {
        DONE, WAITING_SIZE, WAITING_STRING, ERROR
    }

    private State state = State.WAITING_SIZE;
    private final IntReader intReader = new IntReader();
    private final ByteBuffer internalBuffer = ByteBuffer.allocate(BUFFER_SIZE); // write-mode
    private String value;

    @Override
    public ProcessStatus process(ByteBuffer buffer) {
        if (state == State.DONE || state == State.ERROR) {
            throw new IllegalStateException();
        }

        if (state == State.WAITING_SIZE) {
            readSize(buffer);
            if (state == State.ERROR) {
                return ProcessStatus.ERROR;
            }
        }

        if (state != State.WAITING_STRING) {
            return REFILL;
        }

        buffer.flip();
        try {
            if (buffer.remaining() <= internalBuffer.remaining()) {
                internalBuffer.put(buffer);
            } else {
                var oldLimit = buffer.limit();
                buffer.limit(internalBuffer.remaining());
                internalBuffer.put(buffer);
                buffer.limit(oldLimit);
            }
        } finally {
            buffer.compact();
        }

        if (internalBuffer.hasRemaining()) {
            return ProcessStatus.REFILL;
        }
        state = State.DONE;
        internalBuffer.flip();
        value = CHARSET.decode(internalBuffer).toString();
        return DONE;
    }

    private void readSize(ByteBuffer buffer) {
        var status = intReader.process(buffer);
        if (status == DONE) {
            var sizeToRead = intReader.get();
            if (sizeToRead > BUFFER_SIZE || sizeToRead < 0) {
                state = State.ERROR;
                return;
            }
            internalBuffer.limit(sizeToRead);
            intReader.reset();
            state = State.WAITING_STRING;
        }
    }

    @Override
    public String get() {
        if (state != State.DONE) {
            throw new IllegalStateException();
        }
        return value;
    }

    @Override
    public void reset() {
        state = State.WAITING_SIZE;
        internalBuffer.clear();
        intReader.reset();
        value = null;
    }
}
