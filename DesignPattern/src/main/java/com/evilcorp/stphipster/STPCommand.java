package com.evilcorp.stphipster;

public sealed interface STPCommand permits HelloCmd, StartTimerCmd, StopTimerCmd, ElapsedTimeCmd {
}
