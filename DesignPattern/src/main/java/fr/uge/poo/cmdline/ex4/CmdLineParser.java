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

    public void registerWithParameters(String option, int arity, Consumer<List<String>> process) throws IllegalArgumentException {
        requireNonNull(option);
        if (arity < 0) throw new IllegalArgumentException("Arity cannot be negative");
        requireNonNull(process);
        checkOption(option);
        optionToProcess.put(option, new Process(arity, process));
    }

    /**
     * Processes an array of arguments.
     *
     * @param arguments an array of arguments to process
     * @return the list of arguments that are not registered options
     */
    public List<String> process(String[] arguments) {
        var unregistered = new ArrayList<String>();
        var args = List.of(arguments).iterator();

        while (args.hasNext()) {
            var option = args.next();
            if (!optionToProcess.containsKey(option)) {
                if (startsWithDash(option)) throw new IllegalArgumentException("Unregistered option " + option);
                unregistered.add(option);
            } else {
                var process = optionToProcess.get(option);
                var params = new ArrayList<String>();
                for (var i = 0; i < process.arity; i++) {
                    if (!args.hasNext()) throw missingParameter(option);
                    var param = args.next();
                    if (startsWithDash(param)) throw missingParameter(option);
                    params.add(param);
                }
                process.it().accept(params);
            }
        }
        return unregistered;
    }

    private static IllegalArgumentException missingParameter(String option) {
        return new IllegalArgumentException("Missing parameter for option " + option);
    }

    private static boolean startsWithDash(String s) {
        return s.charAt(0) == '-';
    }

    private static record Process(int arity, Consumer<List<String>> it) {
    }
}