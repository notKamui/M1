package fr.uge.poo.visitors.expr.ex2;

/**
 * Contextualized recursive expression visitor that return a value
 *
 * @param <R> the return type of the visitor
 * @param <C> the context type of the visitor
 */
public interface ExprVisitor<R, C> {
    /**
     * Visits a binary operator with a context and returns the result
     *
     * @param binOp the binary operator to visit
     * @param context the context of the visitor
     * @return the result of the visit
     */
    R visit(BinOp binOp, C context);

    /**
     * Visits a value node with a context and returns the result
     *
     * @param value the value node to visit
     * @param context the context of the visitor
     * @return the result of the visit
     */
    R visit(Value value, C context);
}
