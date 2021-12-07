package fr.uge.poo.visitors.expr;

import java.util.Iterator;
import java.util.regex.Pattern;

public class Application {
    public static void main(String[] args) {
        Iterator<String> it = Pattern.compile(" ").splitAsStream("+ * 4 + 1 1 + 2 3").iterator();
        Expr expr = Expr.parseExpr(it);
        var evaluator = new EvalExprVisitor();
        System.out.println(expr);
        System.out.println(expr.accept(evaluator));
    }
}
