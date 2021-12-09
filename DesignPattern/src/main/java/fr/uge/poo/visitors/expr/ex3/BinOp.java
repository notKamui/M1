package fr.uge.poo.visitors.expr.ex3;

import java.util.Objects;
import java.util.function.IntBinaryOperator;

/**
 * Binary operator node
 */
public record BinOp(
    Expr left,
    Expr right,
    String opName,
    IntBinaryOperator operator
) implements Expr {

    /**
     * Constructs and operator node.
     *
     * @param left the left node
     * @param right the right node
     * @param opName the name of the operator
     * @param operator the operation itself
     */
    public BinOp {
        Objects.requireNonNull(left);
        Objects.requireNonNull(right);
        Objects.requireNonNull(opName);
        Objects.requireNonNull(operator);
    }
}
