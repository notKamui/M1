package fr.uge.poo.cmdline.ex1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.util.Objects.requireNonNull;

/**
 * A simple command line parser/processor.
 */
public final class CmdLineParser {

    private final Map<String, Runnable> optionToProcess = new HashMap<>();

    /**
     * Registers an option along with its linked process.
     *
     * @param option the name of the option
     * @param process the runnable process linked to the option
     */
    public void registerFlag(String option, Runnable process) {
        requireNonNull(option);
        requireNonNull(process);
        optionToProcess.put(option, process);
    }

    /**
     * Processes an array of arguments.
     *
     * @param arguments an array of arguments to process
     * @return the list of arguments that are not registered options
     */
    public List<String> process(String[] arguments) {
        var unregistered = new ArrayList<String>();
        for (var argument : arguments) {
            if (optionToProcess.containsKey(argument)) {
                optionToProcess.get(argument).run();
            } else {
                unregistered.add(argument);
            }
        }
        return unregistered;
    }
}