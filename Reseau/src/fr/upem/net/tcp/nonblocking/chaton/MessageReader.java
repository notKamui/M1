package fr.upem.net.tcp.nonblocking.chaton;

import java.nio.ByteBuffer;

import static fr.upem.net.tcp.nonblocking.chaton.Reader.ProcessStatus.*;

public class MessageReader implements Reader<Message> {

    private enum State { DONE, WAITING_USERNAME, WAITING_TEXT, ERROR }

    private State state = State.WAITING_USERNAME;
    private final StringReader stringReader = new StringReader();
    private String username;
    private String text;

    @Override
    public ProcessStatus process(ByteBuffer buffer) {
        if (state == State.DONE || state == State.ERROR) {
            throw new IllegalStateException();
        }

        var status = ERROR;
        if (state == State.WAITING_USERNAME) {
            status = stringReader.process(buffer);
            if (status == DONE) {
                username = stringReader.get();
                stringReader.reset();
                state = State.WAITING_TEXT;
            }
        }

        if (state == State.WAITING_TEXT) {
            status = stringReader.process(buffer);
            if (status == DONE) {
                text = stringReader.get();
                stringReader.reset();
                state = State.DONE;
            }
        }

        return status;
    }

    @Override
    public Message get() {
        if (state != State.DONE) {
            throw new IllegalStateException();
        }
        return new Message(username, text);
    }

    @Override
    public void reset() {
        state = State.WAITING_USERNAME;
        stringReader.reset();
        username = null;
        text = null;
    }
}
