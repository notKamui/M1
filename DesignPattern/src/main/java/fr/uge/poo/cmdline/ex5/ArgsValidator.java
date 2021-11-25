package fr.uge.poo.cmdline.ex5;

import java.util.Objects;
import java.util.Set;

class ArgsValidator {
    private final Set<Option> options;

    ArgsValidator(Set<Option> options) {
        this.options = Objects.requireNonNull(options);
    }

    void validate(String[] args) throws IllegalStateException {
        var argsSet = Set.of(args);

        var missing = options.stream()
            .filter(Option::required)
            .filter(option -> option.names().stream().noneMatch(argsSet::contains))
            .findAny();
        if (missing.isPresent()) {
            throw new IllegalStateException("Missing required option : " + missing.get().names().get(0));
        }
    }
}
