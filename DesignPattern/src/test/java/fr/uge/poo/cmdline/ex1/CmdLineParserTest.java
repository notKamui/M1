package fr.uge.poo.cmdline.ex1;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CmdLineParserTest {

    @Test
    public void invalidOptionRegistration() {
        var parser = new CmdLineParser();
        assertThrows(NullPointerException.class, () -> parser.registerFlag(null, () -> {}));
        assertThrows(NullPointerException.class, () -> parser.registerFlag("", null));
    }

    @Test
    public void processShouldFailFastOnNullArgument(){
        var parser = new CmdLineParser();
        assertThrows(NullPointerException.class, () -> parser.process(null));
    }

    @Test
    public void shouldFailOnAddingTheSameOptionTwice() {
        var parser = new CmdLineParser();
        parser.registerFlag("-a", () -> {});
        assertThrows(IllegalStateException.class, () -> parser.registerFlag("-a", () -> {}));
    }

    @Test
    public void processOnlyUnregistered() {
        var parser = new CmdLineParser();
        String[] args = { "aaa", "bbb", "ccc" };
        assertTrue(parser.process(args).containsAll(List.of("aaa", "bbb", "ccc")));
    }

    @Test
    public void processOnlyFlags() {
        var parser = new CmdLineParser();
        var flags = new HashMap<>(Map.of(
                "a", false,
                "b", false,
                "c", false
        ));
        parser.registerFlag("-a", () -> flags.put("a", true));
        parser.registerFlag("-b", () -> flags.put("b", true));
        parser.registerFlag("-c", () -> flags.put("c", true));

        String[] args0 = { "-b", "-c" };
        parser.process(args0);
        assertFalse(flags.get("a"));
        assertTrue(flags.get("b"));
        assertTrue(flags.get("c"));
    }

    @Test
    public void processAllOptions() {
        var parser = new CmdLineParser();
        var flags = new HashMap<>(Map.of(
                "a", false,
                "b", false,
                "c", false
        ));

        parser.registerFlag("-a", () -> flags.put("a", true));
        parser.registerFlag("-b", () -> flags.put("b", true));
        parser.registerFlag("-c", () -> flags.put("c", true));

        String[] args0 = { "-b", "aaa", "-c", "bbb" };
        var unregistered = parser.process(args0);
        assertFalse(flags.get("a"));
        assertTrue(flags.get("b"));
        assertTrue(flags.get("c"));
        assertTrue(unregistered.containsAll(List.of("aaa", "bbb")));
    }
}