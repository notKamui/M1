package fr.uge.poo.visitors.expr;

public class ToStringVisitor implements ExprVisitor {
    @Override
    public int visit(BinOp binOp) {
        return 0;
    }

    @Override
    public int visit(Value value) {
        return 0;
    }
}
