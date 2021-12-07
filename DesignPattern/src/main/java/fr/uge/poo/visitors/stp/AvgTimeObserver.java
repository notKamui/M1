package fr.uge.poo.visitors.stp;

import com.evilcorp.stp.StartTimerCmd;
import com.evilcorp.stp.StopTimerCmd;

import java.util.ArrayList;
import java.util.HashMap;

public class AvgTimeObserver implements ChronometerObserver {
    private final HashMap<Integer, Long> timers = new HashMap<>();
    private final ArrayList<Long> durations = new ArrayList<>();

    @Override
    public void onStartChrono(StartTimerCmd start, long startTime) {
        timers.put(start.getTimerId(), startTime);
    }

    @Override
    public void onStopChrono(StopTimerCmd stop, long stopTime) {
        durations.add(stopTime - timers.get(stop.getTimerId()));
    }

    @Override
    public void onQuit() {
        var sum = durations.stream().mapToLong(Long::longValue).sum();
        var avg = durations.isEmpty() ? 0 : sum / durations.size();
        System.out.println("Average duration: " + avg + "ms");
    }
}
