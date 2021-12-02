package fr.uge.poo.visitors.stp;

import com.evilcorp.stp.ElapsedTimeCmd;
import com.evilcorp.stp.HelloCmd;
import com.evilcorp.stp.STPCommand;
import com.evilcorp.stp.STPParser;
import com.evilcorp.stp.StartTimerCmd;
import com.evilcorp.stp.StopTimerCmd;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;
import java.util.Scanner;

public class Application {

    public static void main(String[] args) {
        HashMap<Integer,Long> timers = new HashMap<>();
        var scan = new Scanner(System.in);
        while(scan.hasNextLine()){
            var line = scan.nextLine();
            if (line.equals("quit")){
                break;
            }
            Optional<STPCommand> answer = STPParser.parse(line);
            if (answer.isEmpty()){
                System.out.println("Unrecognized command");
                continue;
            }
            STPCommand cmd = answer.get();
            if (cmd instanceof HelloCmd){
                System.out.println("Hello the current date is "+ LocalDateTime.now());
                continue;
            }
            if (cmd instanceof StopTimerCmd stopTimerCmd){
                var timerId = stopTimerCmd.getTimerId();
                var startTime = timers.get(timerId);
                if (startTime==null){
                    System.out.println("Timer "+timerId+" was never started");
                    continue;
                }
                var currentTime =  System.currentTimeMillis();
                System.out.println("Timer "+timerId+" was stopped after running for "+(currentTime-startTime)+"ms");
                timers.put(timerId,null);
                continue;
            }
            if (cmd instanceof StartTimerCmd startTimerCmd){
                var timerId = startTimerCmd.getTimerId();
                if (timers.get(timerId)!=null){
                    System.out.println("Timer "+timerId+" was already started");
                    continue;
                }
                var currentTime =  System.currentTimeMillis();
                timers.put(timerId,currentTime);
                System.out.println("Timer "+timerId+" started");
                continue;

            }
            if (cmd instanceof ElapsedTimeCmd elapsedTimersCmd){
                var currentTime =  System.currentTimeMillis();
                for(var timerId : elapsedTimersCmd.getTimers()){
                    var startTime = timers.get(timerId);
                    if (startTime==null){
                        System.out.println("Unknown timer "+timerId);
                        continue;
                    }
                    System.out.println("Ellapsed time on timerId "+timerId+" : "+(currentTime-startTime)+"ms");
                }
                continue;
            }
        }
    }
}
