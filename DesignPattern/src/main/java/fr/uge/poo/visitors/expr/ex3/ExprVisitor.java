package fr.uge.poo.visitors.expr.ex3;

import java.util.HashMap;
import java.util.function.BiFunction;
import static java.util.Objects.requireNonNull;

/**
 * Contextualized recursive expression visitor that return a value
 *
 * @param <R> the return type of the visitor
 * @param <C> the context type of the visitor
 */
public class ExprVisitor<R, C> {
    private final HashMap<Class<? extends Expr>, BiFunction<Expr, C, R>> applications = new HashMap<>();

    /**
     * Adds a new visitor action on a given type
     * @param type the class type of the subtype of {@link Expr} to apply the action on
     * @param application the application itself that receives the expression node, the context, and returns the result of the application
     * @param <T> the subtype of {@link Expr} to apply this on
     * @return itself
     */
    @SuppressWarnings("unchecked")
    public <T extends Expr> ExprVisitor<R, C> when(Class<T> type, BiFunction<T, C, R> application) {
        requireNonNull(type);
        requireNonNull(application);
        applications.put(type, (BiFunction<Expr, C, R>) application);
        return this;
    }

    /**
     * Visits an expression tree with a context and returns the result
     *
     * @param expr the expression tree to visit
     * @param context the context of the visitor
     * @return the result of the visit
     */
    public R visit(Expr expr, C context) {
        return applications.get(expr.getClass()).apply(expr, context);
    }
}
