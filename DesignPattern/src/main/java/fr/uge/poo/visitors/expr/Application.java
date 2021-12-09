package fr.uge.poo.visitors.expr;

import java.util.regex.Pattern;

public class Application {
    public static void main(String[] args) {
        var it = Pattern.compile(" ").splitAsStream("+ * 4 + 1 1 + 2 3").iterator();
        var expr = Expr.parseExpr(it);

        var evaluator = new EvalExprVisitor();
        System.out.println(expr.accept(evaluator, null));

        var toString = new ToStringVisitor();
        var sb = new StringBuilder();
        expr.accept(toString, sb);
        System.out.println(sb);
    }
}
