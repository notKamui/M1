package com.evilcorp.stp;

public class StartTimerCmd implements STPCommand {

    private final int  timerId;

    public StartTimerCmd(int timerId){
        this.timerId=timerId;
    }

    public int getTimerId() {
        return timerId;
    }
}
