package fr.uge.poo.cmdline.ex5;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.ObjIntConsumer;

import static java.util.Objects.requireNonNull;

public class Option {
    private final List<String> names;
    private final String doc;
    private final int arity;
    private final boolean required;
    private final Consumer<List<String>> process;

    private Option(List<String> names, String doc, int arity, boolean required, Consumer<List<String>> process) {
        this.names = requireNonNull(names);
        this.doc = requireNonNull(doc);
        if (arity < 0) {
            throw new IllegalArgumentException("arity must be >= 0");
        }
        this.arity = arity;
        this.required = required;
        this.process = requireNonNull(process);
    }

    public List<String> names() {
        return List.copyOf(names);
    }

    public String doc() {
        return doc;
    }

    public int arity() {
        return arity;
    }

    public boolean required() {
        return required;
    }

    public Consumer<List<String>> process() {
        return process;
    }

    public static final class Builder {
        private final List<String> names = new ArrayList<>();
        private String doc = "";
        private int arity = 0;
        private boolean required = false;
        private Consumer<List<String>> process = null;

        public Builder addName(String name) {
            requireNonNull(name);
            names.add(name);
            return this;
        }

        public Builder withDoc(String doc) {
            this.doc = requireNonNull(doc);
            return this;
        }

        public Builder isRequired(boolean required) {
            this.required = required;
            return this;
        }

        public Builder withProcess(int arity, Consumer<List<String>> process) {
            if (arity < 0) {
                throw new IllegalArgumentException("arity must be >= 0");
            }
            this.arity = arity;
            this.process = requireNonNull(process);
            return this;
        }

        public Builder withFlag(Runnable process) {
            withProcess(0, args -> process.run());
            return this;
        }

        public Builder withStringProcess(Consumer<String> process) {
            withProcess(1, args -> process.accept(args.get(0)));
            return this;
        }

        public Builder withIntProcess(IntConsumer process) {
            withProcess(1, args -> process.accept(Integer.parseInt(args.get(0))));
            return this;
        }

        public Builder withBiIntProcess(IntBiConsumer process) {
            withProcess(2, args -> process.accept(Integer.parseInt(args.get(0)), Integer.parseInt(args.get(1))));
            return this;
        }

        public Builder withStringIntProcess(ObjIntConsumer<String> process) {
            withProcess(2, args -> process.accept(args.get(0), Integer.parseInt(args.get(1))));
            return this;
        }

        public Option build() {
            if (names.isEmpty()) {
                throw new IllegalStateException("at least one name is required");
            }
            return new Option(names, doc, arity, required, process);
        }
    }
}
