package fr.uge.poo.visitors.expr;

import java.util.Iterator;
import java.util.regex.Pattern;

public class Application {
    public static void main(String[] args) {
        Iterator<String> it = Pattern.compile(" ").splitAsStream("+ * 4 + 1 1 + 2 3").iterator();
        Expr expr = Expr.parseExpr(it);
        var evaluator = new EvalExprVisitor();
        var toString = new ToStringVisitor();
        System.out.println(expr.accept(toString));
        System.out.println(expr.accept(evaluator));
    }
}
