package fr.uge.poo.visitors.expr;

public class EvalExprVisitor implements ExprVisitor {
    @Override
    public int visit(BinOp binOp) {
        return binOp
            .operator()
            .applyAsInt(
                binOp.left().accept(this),
                binOp.right().accept(this)
            );
    }

    @Override
    public int visit(Value value) {
        return value.value();
    }
}
