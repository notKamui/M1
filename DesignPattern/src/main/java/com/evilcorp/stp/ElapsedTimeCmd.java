package com.evilcorp.stp;

import java.util.List;
import java.util.Objects;

public final class ElapsedTimeCmd implements STPCommand {

    private final List<Integer> timers;

    public ElapsedTimeCmd(List<Integer> timers) {
        Objects.requireNonNull(timers);
        this.timers = List.copyOf(timers);
    }

    public List<Integer> getTimers() {
        return timers;
    }

    @Override
    public String name() {
        return "ElapsedTimeCmd";
    }

    @Override
    public void accept(STPCommandVisitor visitor) {
        visitor.visit(this);
    }
}
