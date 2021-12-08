package fr.uge.poo.visitors.expr;

public class ToStringVisitor implements ExprVisitor {
    @Override
    public String visit(BinOp binOp) {
        return "(%s %s %s)".formatted(
            binOp.left().accept(this),
            binOp.opName(),
            binOp.right().accept(this)
        );
    }

    @Override
    public String visit(Value value) {
        return value.value() + "";
    }
}
