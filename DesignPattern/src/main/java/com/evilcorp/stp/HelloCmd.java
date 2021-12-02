package com.evilcorp.stp;

public final class HelloCmd implements STPCommand {

    @Override
    public void accept(STPCommandVisitor visitor) {
        visitor.visit(this);
    }
}
