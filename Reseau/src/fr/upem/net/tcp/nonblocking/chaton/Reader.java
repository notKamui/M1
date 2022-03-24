package fr.upem.net.tcp.nonblocking.chaton;

import java.nio.ByteBuffer;

public interface Reader<T> {

    enum ProcessStatus { DONE, REFILL, ERROR };

    ProcessStatus process(ByteBuffer bb);

    T get();

    void reset();
}
