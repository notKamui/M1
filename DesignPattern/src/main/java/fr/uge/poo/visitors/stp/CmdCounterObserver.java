package fr.uge.poo.visitors.stp;

import com.evilcorp.stp.STPCommand;

import java.util.HashMap;

public class CmdCounterObserver implements ChronometerObserver {
    private final HashMap<String, Integer> cmdCount = new HashMap<>();

    @Override
    public void onCommandCall(STPCommand command) {
        cmdCount.merge(command.name(), 1, Integer::sum);
    }

    @Override
    public void onQuit() {
        cmdCount.forEach((key, value) -> System.out.println("Command " + key + " called " + value + " time(s)"));
    }
}
