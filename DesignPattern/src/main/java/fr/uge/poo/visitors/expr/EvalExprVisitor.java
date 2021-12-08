package fr.uge.poo.visitors.expr;

public class EvalExprVisitor implements ExprVisitor {
    @Override
    public String visit(BinOp binOp) {
        return binOp
            .operator()
            .applyAsInt(
                Integer.parseInt(binOp.left().accept(this)),
                Integer.parseInt(binOp.right().accept(this))
            ) + "";
    }

    @Override
    public String visit(Value value) {
        return value.value() + "";
    }
}
