package com.evilcorp.stp;

public sealed interface STPCommand permits HelloCmd, StartTimerCmd, StopTimerCmd, ElapsedTimeCmd {
    String name();

    void accept(STPCommandVisitor visitor);
}
