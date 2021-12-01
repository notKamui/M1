package fr.uge.poo.cmdline.ex7;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.ObjIntConsumer;

import static java.util.Objects.requireNonNull;


/**
 * Represents an option.
 */
public class Option {
    private final Set<String> names;
    private final String doc;
    private final int arity;
    private final boolean required;
    private final Consumer<List<String>> process;
    private final Set<String> conflicts;

    private Option(Set<String> names, String doc, int arity, boolean required, Consumer<List<String>> process, Set<String> conflicts) {
        this.names = requireNonNull(names);
        this.doc = requireNonNull(doc);
        if (arity < 0) {
            throw new IllegalArgumentException("arity must be >= 0");
        }
        this.arity = arity;
        this.required = required;
        this.process = requireNonNull(process);
        this.conflicts = requireNonNull(conflicts);
    }

    /**
     * The list of names that can be used to invoke this option.
     *
     * @return the list of names
     */
    public List<String> names() {
        return List.copyOf(names);
    }

    /**
     * The documentation string for this option.
     *
     * @return the documentation string
     */
    public String doc() {
        return doc;
    }

    /**
     * The arity of this option.
     *
     * @return the arity
     */
    public int arity() {
        return arity;
    }

    /**
     * The required-ness of this option.
     *
     * @return true if this option is required
     */
    public boolean required() {
        return required;
    }

    /**
     * The actual process of this option.
     *
     * @return the process
     */
    public Consumer<List<String>> process() {
        return process;
    }

    /**
     * The set of conflicting options.
     *
     * @return the set of conflicting options
     */
    public Set<String> conflicts() {
        return Set.copyOf(conflicts);
    }

    @Override
    public String toString() {
        return names().get(0);
    }

    /**
     * Option builder to safely create an option.
     */
    public static final class Builder {
        private final Set<String> names = new HashSet<>();
        private final Set<String> conflicts = new HashSet<>();
        private String doc = "";
        private int arity = 0;
        private boolean required = false;
        private Consumer<List<String>> process = null;

        /**
         * Adds a name to this option.
         *
         * @param name the name to add
         * @return this builder
         */
        public Builder addName(String name) {
            requireNonNull(name);
            names.add(name);
            return this;
        }

        /**
         * Sets the documentation string for this option.
         *
         * @param doc the documentation string
         * @return this builder
         */
        public Builder withDoc(String doc) {
            this.doc = requireNonNull(doc);
            return this;
        }

        /**
         * Sets this option as required if given true.
         *
         * @param required true if this option is required
         * @return this builder
         */
        public Builder isRequired(boolean required) {
            this.required = required;
            return this;
        }

        /**
         * Sets the process for this option.
         *
         * @param arity   the arity of this option
         * @param process the process
         * @return this builder
         */
        public Builder withProcess(int arity, Consumer<List<String>> process) {
            if (arity < 0) {
                throw new IllegalArgumentException("arity must be >= 0");
            }
            this.arity = arity;
            this.process = requireNonNull(process);
            return this;
        }

        /**
         * Sets the process for this option as a flag.
         *
         * @param process the process
         * @return this builder
         */
        public Builder withFlag(Runnable process) {
            withProcess(0, args -> process.run());
            return this;
        }

        /**
         * Sets the process for this option which accepts one string.
         *
         * @param process the process
         * @return this builder
         */
        public Builder withStringProcess(Consumer<String> process) {
            withProcess(1, args -> process.accept(args.get(0)));
            return this;
        }

        /**
         * Sets the process for this option which accepts a single integer.
         *
         * @param process the process
         * @return this builder
         */
        public Builder withIntProcess(IntConsumer process) {
            withProcess(1, args -> process.accept(Integer.parseInt(args.get(0))));
            return this;
        }

        /**
         * Sets the process for this option which accepts two integers.
         *
         * @param process the process
         * @return this builder
         */
        public Builder withBiIntProcess(IntBiConsumer process) {
            withProcess(2, args -> process.accept(Integer.parseInt(args.get(0)), Integer.parseInt(args.get(1))));
            return this;
        }

        /**
         * Sets the process for this option which accepts a single string and a single integer.
         *
         * @param process the process
         * @return this builder
         */
        public Builder withStringIntProcess(ObjIntConsumer<String> process) {
            withProcess(2, args -> process.accept(args.get(0), Integer.parseInt(args.get(1))));
            return this;
        }

        /**
         * Adds a name conflict to this option.
         *
         * @param name the name to conflict with
         * @return this builder
         */
        public Builder addConflict(String name) {
            conflicts.add(name);
            return this;
        }

        /**
         * Builds the option.
         *
         * @return the created option
         */
        public Option build() {
            if (names.isEmpty()) {
                throw new IllegalStateException("at least one name is required");
            }
            return new Option(names, doc, arity, required, process, conflicts);
        }
    }
}
