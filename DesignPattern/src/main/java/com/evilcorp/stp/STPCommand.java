package com.evilcorp.stp;

public sealed interface STPCommand permits HelloCmd, StartTimerCmd, StopTimerCmd, ElapsedTimeCmd {
    void accept(STPCommandVisitor visitor);
}
