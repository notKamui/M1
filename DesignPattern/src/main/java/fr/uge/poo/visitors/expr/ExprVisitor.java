package fr.uge.poo.visitors.expr;

public interface ExprVisitor {
    String visit(BinOp binOp);

    String visit(Value value);
}
