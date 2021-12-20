package fr.uge.poo.visitors.expr.ex2;

/**
 * Expression visitor that returns an {@link Integer}, without context
 * to apply the result of the evaluation of the actual expression.
 */
public class EvalExprVisitor implements ExprVisitor<Integer, Void> {
    @Override
    public Integer visit(BinOp binOp, Void context) {
        return binOp
            .operator()
            .applyAsInt(
                binOp.left().accept(this, null),
                binOp.right().accept(this, null)
            );
    }

    @Override
    public Integer visit(Value value, Void context) {
        return value.value();
    }
}
