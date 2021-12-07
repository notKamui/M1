package fr.uge.poo.visitors.stp;

import com.evilcorp.stp.STPCommand;
import com.evilcorp.stp.StartTimerCmd;
import com.evilcorp.stp.StopTimerCmd;

public interface ChronometerObserver {

    default void onCommandCall(STPCommand command) {
    }

    default void onStartChrono(StartTimerCmd start, long startTime) {
    }

    default void onStopChrono(StopTimerCmd stop, long stopTime) {
    }

    default void onQuit() {
    }
}
