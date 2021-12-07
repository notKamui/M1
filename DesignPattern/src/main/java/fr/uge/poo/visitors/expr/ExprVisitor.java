package fr.uge.poo.visitors.expr;

public interface ExprVisitor {
    int visit(BinOp binOp);

    int visit(Value value);
}
