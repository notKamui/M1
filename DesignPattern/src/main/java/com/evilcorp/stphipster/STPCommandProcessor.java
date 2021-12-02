package com.evilcorp.stphipster;

public interface STPCommandProcessor {
    void process(HelloCmd cmd);

    void process(StartTimerCmd cmd);

    void process(StopTimerCmd cmd);

    void process(ElapsedTimeCmd cmd);

    default void process(STPCommand cmd) {
        switch (cmd) {
            case HelloCmd helloCmd -> process(helloCmd);
            case StartTimerCmd startTimerCmd -> process(startTimerCmd);
            case StopTimerCmd stopTimerCmd -> process(stopTimerCmd);
            case ElapsedTimeCmd elapsedTimeCmd -> process(elapsedTimeCmd);
        }
    }
}
