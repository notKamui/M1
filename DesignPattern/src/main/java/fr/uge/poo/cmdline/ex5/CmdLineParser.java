package fr.uge.poo.cmdline.ex5;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * A simple command line parser/processor.
 */
public final class CmdLineParser {
    private void checkOption(String option) throws IllegalStateException {
        if (nameToOption.containsKey(option))
            throw new IllegalStateException("Option " + option + " is already defined");
    }

    private final Map<String, Option> nameToOption = new HashMap<>();

    /**
     * Registers an option.
     *
     * @param option the option to be registered
     * @throws IllegalStateException if the option is already registered
     */
    public void registerOption(Option option) throws IllegalStateException {
        requireNonNull(option);
        for (var name : option.names()) {
            checkOption(name);
            nameToOption.put(name, option);
        }
    }

    /**
     * Prints the usage of the registered options.
     */
    public void usage() {
        var stringBuilder = new StringBuilder();
        var options = new HashSet<>(nameToOption.values());
        var mainNames = options
            .stream()
            .map(it -> it.names().get(0) + " arg".repeat(Math.max(0, it.arity())))
            .toList();
        stringBuilder
            .append("Usage : command ")
            .append(mainNames.stream().collect(Collectors.joining("] [", "[", "]")))
            .append('\n');

        for (var option : options) {
            stringBuilder
                .append("\t")
                .append(String.join(", ", option.names()))
                .append(" : ")
                .append(option.doc())
                .append(option.required() ? " (REQUIRED)" : "")
                .append("\n");
        }
        System.out.println(stringBuilder);
    }

    /**
     * Registers an option along with its linked process.
     *
     * @param option  the name of the option
     * @param process the runnable process linked to the option
     * @throws IllegalStateException if the option is already registered
     */
    public void registerFlag(String option, Runnable process) throws IllegalStateException {
        registerFlag(option, false, process);
    }

    /**
     * Registers an option along with its linked process.
     *
     * @param option  the name of the option
     * @param required true if the option should be required
     * @param process the runnable process linked to the option
     * @throws IllegalStateException if the option is already registered
     */
    public void registerFlag(String option, boolean required, Runnable process) throws IllegalStateException {
        requireNonNull(option);
        requireNonNull(process);
        checkOption(option);
        nameToOption.put(option, new Option.Builder()
            .addName(option)
            .withDoc("")
            .isRequired(required)
            .withFlag(process)
            .build());
    }

    /**
     * Registers an option with its linked process that receives a single argument.
     *
     * @param option  the name of the option
     * @param process the consumer process linked to the option
     * @throws IllegalStateException if the option is already registered
     */
    public void registerWithParameter(String option, Consumer<String> process) throws IllegalStateException {
        registerWithParameter(option, false, process);
    }

    /**
     * Registers an option with its linked process that receives a single argument.
     *
     * @param option  the name of the option
     * @param required true if the option should be required
     * @param process the consumer process linked to the option
     * @throws IllegalStateException if the option is already registered
     */
    public void registerWithParameter(String option, boolean required, Consumer<String> process) throws IllegalStateException {
        requireNonNull(option);
        requireNonNull(process);
        checkOption(option);
        nameToOption.put(option, new Option.Builder()
            .addName(option)
            .withDoc("")
            .isRequired(required)
            .withStringProcess(process)
            .build());
    }

    /**
     * Registers an option with its linked process that receives a fixed amount of arguments.
     *
     * @param option the name of the option
     * @param arity the arity of the option
     * @param process the consumer process linked to the option
     * @throws IllegalArgumentException if the option is already registered
     */
    public void registerWithParameters(String option, int arity, Consumer<List<String>> process) throws IllegalArgumentException {
        registerWithParameters(option, arity, false, process);
    }

    /**
     * Registers an option with its linked process that receives a fixed amount of arguments.
     *
     * @param option the name of the option
     * @param arity the arity of the option
     * @param required true if the option should be required
     * @param process the consumer process linked to the option
     * @throws IllegalArgumentException if the option is already registered
     */
    public void registerWithParameters(String option, int arity, boolean required, Consumer<List<String>> process) throws IllegalArgumentException {
        requireNonNull(option);
        if (arity < 0) throw new IllegalArgumentException("Arity cannot be negative");
        requireNonNull(process);
        checkOption(option);
        nameToOption.put(option, new Option.Builder()
            .addName(option)
            .withDoc("")
            .isRequired(required)
            .withProcess(arity, process)
            .build());
    }

    /**
     * Processes an array of arguments.
     *
     * @param arguments an array of arguments to process
     * @return the list of arguments that are not registered options
     * @throws IllegalArgumentException if the list of arguments either contain an unregistered option or one option is missing a parameter
     * @throws IllegalStateException    if one required option has not been set
     */
    public List<String> process(String[] arguments) throws IllegalArgumentException, IllegalStateException {
        var unregistered = new ArrayList<String>();
        var args = List.of(arguments).iterator();
        var seen = new ArrayList<String>();

        while (args.hasNext()) {
            var option = args.next();
            if (!nameToOption.containsKey(option)) {
                if (startsWithDash(option)) throw new IllegalArgumentException("Unregistered option " + option);
                unregistered.add(option);
            } else {
                seen.add(option);
                var actualOption = nameToOption.get(option);
                var params = new ArrayList<String>();
                for (var i = 0; i < actualOption.arity(); i++) {
                    if (!args.hasNext()) throw missingParameter(option);
                    var param = args.next();
                    if (startsWithDash(param)) throw missingParameter(option);
                    params.add(param);
                }
                actualOption.process().accept(params);
            }
        }

        var requiredAreAllSeen = nameToOption
                .entrySet()
                .stream()
                .filter(it -> it.getValue().required())
                .map(Map.Entry::getKey)
                .allMatch(seen::contains);
        if (!requiredAreAllSeen) throw new IllegalStateException("Some options have not been set");
        return unregistered;
    }

    private static IllegalArgumentException missingParameter(String option) {
        return new IllegalArgumentException("Missing parameter for option " + option);
    }

    private static boolean startsWithDash(String s) {
        return s.charAt(0) == '-';
    }
}