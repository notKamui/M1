package fr.uge.poo.cmdline.ex7;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * A simple command line parser/processor.
 */
public final class CmdLineParser {
    private final OptionManager om;
    private final DocumentationObserver doc;

    public CmdLineParser() {
        om = new OptionManager();
        //om.registerObserver(new LoggerObserver());
        om.registerObserver(new RequiredOptionObserver());
        this.doc = new DocumentationObserver();
        om.registerObserver(doc);
        om.registerObserver(new OptionConflictObserver());
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
     * @param option   the name of the option
     * @param required true if the option should be required
     * @param process  the runnable process linked to the option
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
     * @param option   the name of the option
     * @param required true if the option should be required
     * @param process  the consumer process linked to the option
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

    private static boolean startsWithDash(String s) {
        return s.charAt(0) == '-';
    }

    /**
     * Registers an option with its linked process that receives a fixed amount of arguments.
     *
     * @param option  the name of the option
     * @param arity   the arity of the option
     * @param process the consumer process linked to the option
     * @throws IllegalStateException if the option is already registered
     */
    @Deprecated
    public void registerWithParameters(String option, int arity, Consumer<List<String>> process) throws IllegalStateException {
        registerWithParameters(option, arity, false, process);
    }

    /**
     * Registers an option with its linked process that receives a fixed amount of arguments.
     *
     * @param option   the name of the option
     * @param arity    the arity of the option
     * @param required true if the option should be required
     * @param process  the consumer process linked to the option
     * @throws IllegalStateException if the option is already registered
     */
    @Deprecated
    public void registerWithParameters(String option, int arity, boolean required, Consumer<List<String>> process) throws IllegalStateException {
        requireNonNull(option);
        if (arity < 0) throw new IllegalStateException("Arity cannot be negative");
        requireNonNull(process);
        om.register(option, new Option.Builder()
            .addName(option)
            .withDoc("")
            .isRequired(required)
            .withProcess(arity, process)
            .build());
    }

    /**
     * Processes an array of arguments with the STANDARD {@link ParameterRetrievalStrategy} policy.
     *
     * @param arguments an array of arguments to process
     * @return the list of arguments that are not registered options
     * @throws ParseException if an error while processing the arguments occurs
     */
    public List<String> process(String[] arguments) throws ParseException {
        return process(arguments, ParameterRetrievalStrategy.STANDARD);
    }

    /**
     * Processes an array of arguments.
     *
     * @param arguments         an array of arguments to process
     * @param retrievalStrategy the retrieval strategy to use
     * @return the list of arguments that are not registered options
     * @throws ParseException if an error while processing the arguments occurs
     */
    public List<String> process(String[] arguments, ParameterRetrievalStrategy retrievalStrategy) throws ParseException {
        var unregistered = new ArrayList<String>();
        var args = PeekIterator.of(List.of(arguments));
        while (args.hasNext()) {
            var option = args.next();
            System.out.println(option);
            var proc = om.processOption(option);
            if (proc.isEmpty()) {
                if (startsWithDash(option)) throw new ParseException("Unregistered option " + option, 0);
                unregistered.add(option);
            } else {
                var actualOption = proc.get();
                var params = retrievalStrategy.retrieveParameter(args, actualOption, option, om.nameToOption.keySet());
                actualOption.process().accept(params);
            }
        }

        om.finishProcess();
        return unregistered;
    }

    private interface OptionManagerObserver {
        default void onRegisteredOption(OptionManager optionManager, Option option) {
        }

        default void onProcessedOption(OptionManager optionManager, Option option) {
        }

        default void onFinishedProcess(OptionManager optionManager) throws ParseException {
        }
    }

    private static final class OptionManager {
        private final HashMap<String, Option> nameToOption = new HashMap<>();
        private final ArrayList<OptionManagerObserver> omObservers = new ArrayList<>();
        private final HashSet<Option> seen = new HashSet<>();

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
        void register(Option option) throws IllegalStateException {
            for (var name : option.names()) {
                register(name, option);
            }
        }

        private void register(String name, Option option) throws IllegalStateException {
            for (var observer : omObservers) {
                observer.onRegisteredOption(this, option);
            }
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
            if (option.isPresent()) {
                for (var observer : omObservers) {
                    observer.onProcessedOption(this, option.get());
                }
                seen.add(option.get());
            }
            return option;
        }

        /**
         * This method is called to signal the method process of the CmdLineParser is finished
         */
        void finishProcess() throws ParseException {
            for (var observer : omObservers) {
                observer.onFinishedProcess(this);
            }
        }

        Set<Option> seen() {
            return Set.copyOf(seen);
        }
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

        @Override
        public void onRegisteredOption(OptionManager optionManager, Option option) {
            if (!option.required()) return;
            required.add(option);
        }

        @Override
        public void onFinishedProcess(OptionManager optionManager) throws ParseException {
            var seen = optionManager.seen();
            var missing = required.stream()
                .filter(option -> !seen.contains(option))
                .findAny();
            if (missing.isPresent()) {
                throw new ParseException("Missing required option : " + missing.get().names().get(0), 0);
            }
        }
    }

    private static class DocumentationObserver implements OptionManagerObserver {
        private final StringJoiner documentation = new StringJoiner("\n").add("Usage :");

        @Override
        public void onRegisteredOption(OptionManager optionManager, Option option) {
            documentation.add("\t%s : %s".formatted(option.names().get(0), option.doc()));
        }

        String usage() {
            return documentation.toString();
        }
    }

    private static class OptionConflictObserver implements OptionManagerObserver {

        @Override
        public void onFinishedProcess(OptionManager optionManager) throws ParseException {
            var seen = optionManager.seen();
            var seenNames = seen.stream().flatMap(option -> option.names().stream()).collect(Collectors.toSet());
            for (var option : seen) {
                if (option.conflicts().isEmpty()) continue;
                for (var conflict : option.conflicts()) {
                    if (seenNames.contains(conflict)) {
                        throw new ParseException("Conflicting options : " + option.names().get(0) + " and " + conflict, 0);
                    }
                }
            }
        }
    }
}