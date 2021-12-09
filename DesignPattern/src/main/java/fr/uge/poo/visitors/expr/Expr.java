package fr.uge.poo.visitors.expr;

import java.util.Iterator;

/**
 * Represents an expression node
 */
public interface Expr {

    /**
     * Parses an iterator of strings into an expression tree.
     *
     * @param it the iterator of strings to parse
     * @return the expression tree
     */
    static Expr parseExpr(Iterator<String> it) {
        if (!it.hasNext()) {
            throw new IllegalArgumentException("no more tokens");
        }
        String token = it.next();
        return switch (token) {
            case "+" -> new BinOp(parseExpr(it), parseExpr(it), token, (a, b) -> a + b);
            case "-" -> new BinOp(parseExpr(it), parseExpr(it), token, (a, b) -> a - b);
            case "*" -> new BinOp(parseExpr(it), parseExpr(it), token, (a, b) -> a * b);
            case "/" -> new BinOp(parseExpr(it), parseExpr(it), token, (a, b) -> a / b);
            default -> new Value(Integer.parseInt(token));
        };
    }

    /**
     * Accepts and apply an {@link ExprVisitor} with its given return and context types, with the context.
     *
     * @param visitor the visitor to accept and apply
     * @param context the context of the visitor
     * @param <R> the return type of the visitor
     * @param <C> the context type of the visitor
     * @return the result of the visit
     */
    <R, C> R accept(ExprVisitor<R, C> visitor, C context);
}
