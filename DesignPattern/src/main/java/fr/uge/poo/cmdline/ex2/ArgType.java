package fr.uge.poo.cmdline.ex2;

import java.util.List;
import java.util.function.Consumer;

public sealed interface ArgType {
    record Flag(Runnable process) implements ArgType {}

    record ParamOption(int arity, Consumer<List<String>> process) implements ArgType {}
}
