package com.evilcorp.stp;

public final class HelloCmd implements STPCommand {

    @Override
    public String name() {
        return "HelloCmd";
    }

    @Override
    public void accept(STPCommandVisitor visitor) {
        visitor.visit(this);
    }
}
