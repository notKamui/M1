package fr.uge.poo.visitors.expr;

public record Value(int value) implements Expr {

    @Override
    public String toString() {
        return Integer.toString(value);
    }

    @Override
    public String accept(ExprVisitor visitor) {
        return visitor.visit(this);
    }
}
