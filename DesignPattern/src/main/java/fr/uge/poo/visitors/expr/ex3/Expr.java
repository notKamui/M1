package fr.uge.poo.visitors.expr.ex3;

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
}
