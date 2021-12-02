package fr.uge.poo.visitors.stp;

import com.evilcorp.stp.ElapsedTimeCmd;
import com.evilcorp.stp.HelloCmd;
import com.evilcorp.stp.STPCommandVisitor;
import com.evilcorp.stp.STPParser;
import com.evilcorp.stp.StartTimerCmd;
import com.evilcorp.stp.StopTimerCmd;
import java.util.Scanner;

public class Triviale {
    
    public static void main(String[] args) {
        var scanner = new Scanner(System.in);
        var cmdVisitor = new STPCommandVisitor() {
            @Override
            public void visit(HelloCmd cmd) {
                System.out.println("Au revoir");
            }

            @Override
            public void visit(StartTimerCmd cmd) {
                System.out.println("Non implémenté");
            }

            @Override
            public void visit(StopTimerCmd cmd) {
                System.out.println("Non implémenté");
            }

            @Override
            public void visit(ElapsedTimeCmd cmd) {
                System.out.println("Non implémenté");
            }
        };
        while (scanner.hasNextLine()) {
            var line = scanner.nextLine();
            if (line.equals("quit")) break;
            var answer = STPParser.parse(line);
            if (answer.isEmpty()) {
                System.out.println("Pas compris");
                continue;
            }
            var actual = answer.get();
            actual.accept(cmdVisitor);
        }
    }
}
