package fr.uge.poo.cmdline.ex5;

import java.util.List;
import java.util.function.Consumer;

public interface Option {
    List<String> names();
    String doc();
    int arity();
    boolean required();
    Consumer<List<String>> process();

    record Flag(List<String> names, String doc, boolean required, Runnable run) implements Option {
        public Flag(String name, String doc, boolean required, Runnable run) {
            this(List.of(name), doc, required, run);
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

    record SimpleOption(List<String> names, String doc, boolean required, Consumer<String> consumer) implements Option {
        public SimpleOption(String name, String doc, boolean required, Consumer<String> consumer) {
            this(List.of(name), doc, required, consumer);
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

    record ComplexOption(List<String> names, String doc, int arity, boolean required, Consumer<List<String>> consumer) implements Option {
        public ComplexOption(String name, String doc, int arity, boolean required, Consumer<List<String>> consumer) {
            this(List.of(name), doc, arity, required, consumer);
        }

        @Override
        public Consumer<List<String>> process() {
            return consumer;
        }
    }
}
