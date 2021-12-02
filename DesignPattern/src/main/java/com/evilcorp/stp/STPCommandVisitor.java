package com.evilcorp.stp;

public interface STPCommandVisitor {
    void visit(HelloCmd cmd);

    void visit(StartTimerCmd cmd);

    void visit(StopTimerCmd cmd);

    void visit(ElapsedTimeCmd cmd);
}
