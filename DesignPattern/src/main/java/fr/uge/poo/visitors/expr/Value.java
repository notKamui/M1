package fr.uge.poo.visitors.expr;

/**
 * Value node
 */
public record Value(int value) implements Expr {

    @Override
    public <R, C> R accept(ExprVisitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }
}
