package fr.uge.poo.cmdline.ex6;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

/**
 * A simple command line parser/processor.
 */
public final class CmdLineParser {
    private final OptionManager om;
    private final DocumentationObserver doc;

    public CmdLineParser() {
        om = new OptionManager();
        om.registerObserver(new LoggerObserver());
        om.registerObserver(new RequiredOptionObserver());
        this.doc = new DocumentationObserver();
        om.registerObserver(doc);
    }

    /**
     * Prints the usage message.
     */
    public void usage() {
        System.out.println(doc.usage());
    }

    /**
     * Registers an option.
     *
     * @param option the option to be registered
     * @throws IllegalStateException if the option is already registered
     */
    public void registerOption(Option option) throws IllegalStateException {
        requireNonNull(option);
        om.register(option);
    }

    /**
     * Registers an option along with its linked process.
     *
     * @param option  the name of the option
     * @param process the runnable process linked to the option
     * @throws IllegalStateException if the option is already registered
     */
    @Deprecated
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
    @Deprecated
    public void registerFlag(String option, boolean required, Runnable process) throws IllegalStateException {
        requireNonNull(option);
        requireNonNull(process);
        om.register(option, new Option.Builder()
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
    @Deprecated
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
    @Deprecated
    public void registerWithParameter(String option, boolean required, Consumer<String> process) throws IllegalStateException {
        requireNonNull(option);
        requireNonNull(process);
        om.register(option, new Option.Builder()
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
    @Deprecated
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
    @Deprecated
    public void registerWithParameters(String option, int arity, boolean required, Consumer<List<String>> process) throws IllegalArgumentException {
        requireNonNull(option);
        if (arity < 0) throw new IllegalArgumentException("Arity cannot be negative");
        requireNonNull(process);
        om.register(option, new Option.Builder()
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

        while (args.hasNext()) {
            var option = args.next();
            var proc = om.processOption(option);
            if (proc.isEmpty()) {
                if (startsWithDash(option)) throw new IllegalArgumentException("Unregistered option " + option);
                unregistered.add(option);
            } else {
                var actualOption = proc.get();
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

        om.finishProcess();
        return unregistered;
    }

    private static IllegalArgumentException missingParameter(String option) {
        return new IllegalArgumentException("Missing required option : " + option);
    }

    private static boolean startsWithDash(String s) {
        return s.charAt(0) == '-';
    }

    private static final class OptionManager {
        private final HashMap<String, Option> nameToOption = new HashMap<>();
        private final ArrayList<OptionManagerObserver> omObservers = new ArrayList<>();

        /**
         * Registers a new observer to the manager.
         *
         * @param observer the observer to be registered
         */
        void registerObserver(OptionManagerObserver observer) {
            omObservers.add(requireNonNull(observer));
        }

        /**
         * Register the option with all its possible names
         *
         * @param option the option to register
         */
        void register(Option option) {
            for (var name : option.names()) {
                register(name, option);
            }
        }

        private void register(String name, Option option) {
            omObservers.forEach(observer -> observer.onRegisteredOption(this, option));
            if (nameToOption.containsKey(name)) {
                throw new IllegalStateException("Option " + name + " is already registered.");
            }
            nameToOption.put(name, option);
        }

        /**
         * This method is called to signal that an option is encountered during
         * a command line process
         *
         * @param optionName the name of the option
         * @return the corresponding object option if it exists
         */

        Optional<Option> processOption(String optionName) {
            var option = Optional.ofNullable(nameToOption.get(optionName));
            option.ifPresent(o -> omObservers.forEach(observer -> observer.onProcessedOption(this, o)));
            return option;
        }

        /**
         * This method is called to signal the method process of the CmdLineParser is finished
         */
        void finishProcess() {
            omObservers.forEach(observer -> observer.onFinishedProcess(this));
        }

        Set<Option> options() {
            return new HashSet<>(nameToOption.values());
        }
    }

    private interface OptionManagerObserver {
        default void onRegisteredOption(OptionManager optionManager, Option option) {}

        default void onProcessedOption(OptionManager optionManager, Option option) {}

        default void onFinishedProcess(OptionManager optionManager) {}
    }

    private static class LoggerObserver implements OptionManagerObserver {

        @Override
        public void onRegisteredOption(OptionManager optionManager, Option option) {
            System.out.println("Option " + option + " is registered");
        }

        @Override
        public void onProcessedOption(OptionManager optionManager, Option option) {
            System.out.println("Option " + option + " is processed");
        }

        @Override
        public void onFinishedProcess(OptionManager optionManager) {
            System.out.println("Process method is finished");
        }
    }

    private static class RequiredOptionObserver implements OptionManagerObserver {
        private final HashSet<Option> required = new HashSet<>();
        private final HashSet<Option> seen = new HashSet<>();

        @Override
        public void onRegisteredOption(OptionManager optionManager, Option option) {
            if (!option.required()) return;
            required.add(option);
        }

        @Override
        public void onProcessedOption(OptionManager optionManager, Option option) {
            seen.add(option);
        }

        @Override
        public void onFinishedProcess(OptionManager optionManager) {
            var missing = required.stream()
                .filter(option -> !seen.contains(option))
                .findAny();
            if (missing.isPresent()) {
                throw new IllegalStateException("Missing required option : " + missing.get().names().get(0));
            }
        }
    }

    private static class DocumentationObserver implements OptionManagerObserver {
        private final StringJoiner documentation = new StringJoiner("\n").add("Usage :");

        @Override
        public void onRegisteredOption(OptionManager optionManager, Option option) {
            documentation.add("- %s : %s".formatted(option.names().get(0), option.doc()));
        }

        String usage() {
            return documentation.toString();
        }
    }

    private static class OptionConflictObserver implements OptionManagerObserver {
        private final HashSet<String> seen = new HashSet<>();

        @Override
        public void onRegisteredOption(OptionManager optionManager, Option option) {
            if (option.conflicts().stream().anyMatch(seen::contains)) {
                throw new IllegalStateException("Option " + option.names().get(0) + " conflicts with an already registered option");
            }
            seen.addAll(option.names());
        }
    }
}