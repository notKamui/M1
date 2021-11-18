package fr.uge.poo.cmdline.ex5;

import java.util.List;
import java.util.function.Consumer;

public interface Option {
    List<String> names();
    int arity();
    boolean required();
    Consumer<List<String>> process();

    record Flag(List<String> names, boolean required, Runnable run) implements Option {
        public Flag(String name, boolean required, Runnable run) {
            this(List.of(name), required, run);
        }

        @Override
        public int arity() {
            return 0;
        }

        @Override
        public Consumer<List<String>> process() {
            return ignored -> run();
        }
    }

    record SimpleOption(List<String> names, boolean required, Consumer<String> consumer) implements Option {
        public SimpleOption(String name, boolean required, Consumer<String> consumer) {
            this(List.of(name), required, consumer);
        }

        @Override
        public int arity() {
            return 1;
        }

        @Override
        public Consumer<List<String>> process() {
            return args -> consumer.accept(args.get(0));
        }
    }

    record ComplexOption(List<String> names, int arity, boolean required, Consumer<List<String>> consumer) implements Option {
        public ComplexOption(String name, int arity, boolean required, Consumer<List<String>> consumer) {
            this(List.of(name), arity, required, consumer);
        }

        @Override
        public Consumer<List<String>> process() {
            return consumer;
        }
    }
}
