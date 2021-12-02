package com.evilcorp.stphipster;

import java.util.List;
import java.util.Objects;

public record ElapsedTimeCmd(List<Integer> timers) implements STPCommand {
    public ElapsedTimeCmd(List<Integer> timers) {
        Objects.requireNonNull(timers);
        this.timers = List.copyOf(timers);
    }
}
