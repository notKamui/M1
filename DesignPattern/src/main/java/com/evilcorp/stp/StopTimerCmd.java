package com.evilcorp.stp;

public class StopTimerCmd implements STPCommand {

    private final int  timerId;

    public StopTimerCmd(int timerId){
        this.timerId=timerId;
    }

    public int getTimerId() {
        return timerId;
    }
}
