package fr.uge.poo.visitors.expr.ex2;

/**
 * Expression visitor that returns nothing relevant, and with a unique {@link StringBuilder} context
 * to apply the infix expression.
 */
public class ToStringVisitor implements ExprVisitor<Void, StringBuilder> {
    @Override
    public Void visit(BinOp binOp, StringBuilder context) {
        context.append('(');
        binOp.left().accept(this, context);
        context.append(' ').append(binOp.opName()).append(' ');
        binOp.right().accept(this, context);
        context.append(')');
        return null;
    }

    @Override
    public Void visit(Value value, StringBuilder context) {
        context.append(value.value());
        return null;
    }
}
