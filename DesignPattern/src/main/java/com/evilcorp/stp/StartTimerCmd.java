package com.evilcorp.stp;

public final class StartTimerCmd implements STPCommand {

    private final int timerId;

    public StartTimerCmd(int timerId) {
        this.timerId = timerId;
    }

    public int getTimerId() {
        return timerId;
    }

    @Override
    public String name() {
        return "StartTimerCmd";
    }

    @Override
    public void accept(STPCommandVisitor visitor) {
        visitor.visit(this);
    }
}
