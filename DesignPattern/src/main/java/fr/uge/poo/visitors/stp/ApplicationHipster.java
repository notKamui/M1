package fr.uge.poo.visitors.stp;

import com.evilcorp.stphipster.ElapsedTimeCmd;
import com.evilcorp.stphipster.HelloCmd;
import com.evilcorp.stphipster.STPCommand;
import com.evilcorp.stphipster.STPCommandProcessor;
import com.evilcorp.stphipster.STPParser;
import com.evilcorp.stphipster.StartTimerCmd;
import com.evilcorp.stphipster.StopTimerCmd;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;
import java.util.Scanner;

public class ApplicationHipster {
    public static void main(String[] args) {
        var scan = new Scanner(System.in);
        var cmdProcessor = new STPCommandProcessor() {
            private final HashMap<Integer, Long> timers = new HashMap<>();

            @Override
            public void process(HelloCmd cmd) {
                System.out.println("Hello the current date is "+ LocalDateTime.now());
            }

            @Override
            public void process(StartTimerCmd cmd) {
                var timerId = cmd.timerId();
                if (timers.get(timerId) != null){
                    System.out.println("Timer "+timerId+" was already started");
                    return;
                }
                var currentTime =  System.currentTimeMillis();
                timers.put(timerId,currentTime);
                System.out.println("Timer "+timerId+" started");
            }

            @Override
            public void process(StopTimerCmd cmd) {
                var timerId = cmd.timerId();
                var startTime = timers.get(timerId);
                if (startTime == null){
                    System.out.println("Timer " + timerId + " was never started");
                    return;
                }
                var currentTime =  System.currentTimeMillis();
                System.out.println("Timer " + timerId + " was stopped after running for " + (currentTime - startTime) + "ms");
                timers.put(timerId, null);
            }

            @Override
            public void process(ElapsedTimeCmd cmd) {
                var currentTime =  System.currentTimeMillis();
                for(var timerId : cmd.timers()){
                    var startTime = timers.get(timerId);
                    if (startTime == null){
                        System.out.println("Unknown timer "+timerId);
                        continue;
                    }
                    System.out.println("Elapsed time on timerId " + timerId + " : " + (currentTime - startTime) + "ms");
                }
            }
        };
        while(scan.hasNextLine()){
            var line = scan.nextLine();
            if (line.equals("quit")) {
                break;
            }
            Optional<STPCommand> answer = STPParser.parse(line);
            if (answer.isEmpty()){
                System.out.println("Unrecognized command");
                continue;
            }
            STPCommand cmd = answer.get();
            cmdProcessor.process(cmd);
        }
    }
}
