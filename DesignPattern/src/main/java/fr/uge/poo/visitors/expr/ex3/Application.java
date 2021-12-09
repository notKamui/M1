package fr.uge.poo.visitors.expr.ex3;

import java.util.regex.Pattern;

public class Application {
    public static void main(String[] args) {
        var it = Pattern.compile(" ").splitAsStream("+ * 4 + 1 1 + 2 3").iterator();
        var expr = Expr.parseExpr(it);

        var evaluator = new ExprVisitor<Integer, Object>();
        evaluator
            .when(BinOp.class, (binOp, context) -> binOp
                .operator()
                .applyAsInt(
                    evaluator.visit(binOp.left(), null),
                    evaluator.visit(binOp.right(), null)
                )
            )
            .when(Value.class, (value, context) -> value.value());
        System.out.println(evaluator.visit(expr, null));

        var toString = new ExprVisitor<Object, StringBuilder>();
        toString
            .when(BinOp.class, (binOp, context) -> {
                context.append('(');
                toString.visit(binOp.left(), context);
                context.append(' ').append(binOp.opName()).append(' ');
                toString.visit(binOp.right(), context);
                context.append(')');
                return null;
            })
            .when(Value.class, (value, context) -> {
                context.append(value.value());
                return null;
            });
        var sb = new StringBuilder();
        toString.visit(expr, sb);
        System.out.println(sb);
    }
}
