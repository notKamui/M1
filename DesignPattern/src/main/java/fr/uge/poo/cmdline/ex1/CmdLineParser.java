package fr.uge.poo.cmdline.ex1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.util.Objects.requireNonNull;

public class CmdLineParser {

    private final Map<String, Runnable> optionToProcess = new HashMap<>();

    public void registerOption(String option, Runnable process) {
        requireNonNull(option);
        requireNonNull(process);
        optionToProcess.put(option, process);
    }

    public List<String> process(String[] arguments) {
        ArrayList<String> files = new ArrayList<>();
        for (String argument : arguments) {
            if (optionToProcess.containsKey(argument)) {
                optionToProcess.get(argument).run();
            } else {
                files.add(argument);
            }
        }
        return files;
    }
}