package fr.uge.poo.cmdline.ex4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import static java.util.Objects.requireNonNull;

/**
 * A simple command line parser/processor.
 */
public final class CmdLineParser {
    private void checkOption(String option) throws IllegalStateException {
        if (optionToProcess.containsKey(option))
            throw new IllegalStateException("Option " + option + " is already defined");
    }

    private final Map<String, Process> optionToProcess = new HashMap<>();

    /**
     * Registers an option along with its linked process.
     *
     * @param option  the name of the option
     * @param process the runnable process linked to the option
     * @throws IllegalStateException if the option is already registered
     */
    public void registerFlag(String option, Runnable process) throws IllegalStateException {
        requireNonNull(option);
        requireNonNull(process);
        checkOption(option);
        optionToProcess.put(option, new Process(0, (ignored) -> process.run()));
    }

    /**
     * Registers an option with its linked process that receives a single argument.
     *
     * @param option  the name of the option
     * @param process the consumer process linked to the option
     * @throws IllegalStateException if the option is already registered
     */
    public void registerWithParameter(String option, Consumer<String> process) throws IllegalStateException {
        requireNonNull(option);
        requireNonNull(process);
        checkOption(option);
        optionToProcess.put(option, new Process(1, (args) -> process.accept(args.get(0))));
    }

    /**
     * Processes an array of arguments.
     *
     * @param arguments an array of arguments to process
     * @return the list of arguments that are not registered options
     */
    public List<String> process(String[] arguments) {
        var unregistered = new ArrayList<String>();
        for (int i = 0; i < arguments.length; i++) {
            var argument = arguments[i];
            if (optionToProcess.containsKey(argument)) {
                var process = optionToProcess.get(argument);
                if (process.arity() > 0) {
                    var subArgs = List.of(arguments).subList(i + 1, i + 1 + process.arity());
                    process.it().accept(subArgs);
                    i += subArgs.size();
                } else {
                    process.it().accept(null);
                }
            } else {
                unregistered.add(argument);
            }
        }
        return unregistered;
    }

    private static record Process(int arity, Consumer<List<String>> it) {
    }
}