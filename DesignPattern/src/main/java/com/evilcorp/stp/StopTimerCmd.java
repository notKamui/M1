package com.evilcorp.stp;

public final class StopTimerCmd implements STPCommand {

    private final int timerId;

    public StopTimerCmd(int timerId) {
        this.timerId = timerId;
    }

    public int getTimerId() {
        return timerId;
    }

    @Override
    public String name() {
        return "StopTimerCmd";
    }

    @Override
    public void accept(STPCommandVisitor visitor) {
        visitor.visit(this);
    }
}
