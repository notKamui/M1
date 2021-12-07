package fr.uge.poo.visitors.stp;

import com.evilcorp.stp.ElapsedTimeCmd;
import com.evilcorp.stp.HelloCmd;
import com.evilcorp.stp.STPCommand;
import com.evilcorp.stp.STPCommandVisitor;
import com.evilcorp.stp.STPParser;
import com.evilcorp.stp.StartTimerCmd;
import com.evilcorp.stp.StopTimerCmd;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Application {

    public static void main(String[] args) {
        var scan = new Scanner(System.in);
        var observers = List.of(
            new CmdCounterObserver(),
            new AvgTimeObserver()
        );
        var cmdVisitor = new STPCommandVisitor() {
            private final HashMap<Integer, Long> timers = new HashMap<>();

            @Override
            public void visit(HelloCmd cmd) {
                System.out.println("Hello the current date is " + LocalDateTime.now());
                observers.forEach(o -> o.onCommandCall(cmd));
            }

            @Override
            public void visit(StartTimerCmd cmd) {
                var timerId = cmd.getTimerId();
                if (timers.get(timerId) != null) {
                    System.out.println("Timer " + timerId + " was already started");
                    return;
                }
                var currentTime = System.currentTimeMillis();
                timers.put(timerId, currentTime);
                System.out.println("Timer " + timerId + " started");
                observers.forEach(o -> {
                    o.onCommandCall(cmd);
                    o.onStartChrono(cmd, currentTime);
                });
            }

            @Override
            public void visit(StopTimerCmd cmd) {
                var timerId = cmd.getTimerId();
                var startTime = timers.get(timerId);
                if (startTime == null) {
                    System.out.println("Timer " + timerId + " was never started");
                    return;
                }
                var currentTime = System.currentTimeMillis();
                System.out.println("Timer " + timerId + " was stopped after running for " + (currentTime - startTime) + "ms");
                timers.put(timerId, null);
                observers.forEach(o -> {
                    o.onCommandCall(cmd);
                    o.onStopChrono(cmd, currentTime);
                });
            }

            @Override
            public void visit(ElapsedTimeCmd cmd) {
                var currentTime =  System.currentTimeMillis();
                for(var timerId : cmd.getTimers()){
                    var startTime = timers.get(timerId);
                    if (startTime == null){
                        System.out.println("Unknown timer "+timerId);
                        continue;
                    }
                    System.out.println("Elapsed time on timerId " + timerId + " : " + (currentTime - startTime) + "ms");
                }
                observers.forEach(o -> o.onCommandCall(cmd));
            }
        };
        while(scan.hasNextLine()) {
            var line = scan.nextLine();
            if (line.equals("quit")) {
                observers.forEach(ChronometerObserver::onQuit);
                break;
            }
            Optional<STPCommand> answer = STPParser.parse(line);
            if (answer.isEmpty()) {
                System.out.println("Unrecognized command");
                continue;
            }
            STPCommand cmd = answer.get();
            cmd.accept(cmdVisitor);
        }
    }
}
