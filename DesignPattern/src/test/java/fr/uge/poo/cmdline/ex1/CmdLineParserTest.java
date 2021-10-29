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
    public void processShouldFailFastOnNullArgument(){
        var parser = new CmdLineParser();
        assertThrows(NullPointerException.class, () -> parser.process(null));
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
        parser.registerOption("-a", () -> flags.put("a", true));
        parser.registerOption("-b", () -> flags.put("b", true));
        parser.registerOption("-c", () -> flags.put("c", true));

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

        parser.registerOption("-a", () -> flags.put("a", true));
        parser.registerOption("-b", () -> flags.put("b", true));
        parser.registerOption("-c", () -> flags.put("c", true));

        String[] args0 = { "-b", "aaa", "-c", "bbb" };
        var unregistered = parser.process(args0);
        assertFalse(flags.get("a"));
        assertTrue(flags.get("b"));
        assertTrue(flags.get("c"));
        assertTrue(unregistered.containsAll(List.of("aaa", "bbb")));
    }
}