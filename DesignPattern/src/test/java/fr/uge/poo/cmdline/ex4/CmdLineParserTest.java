package fr.uge.poo.cmdline.ex4;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
        String[] args = {"aaa", "bbb", "ccc"};
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

        String[] args0 = {"-b", "-c"};
        parser.process(args0);
        assertFalse(flags.get("a"));
        assertTrue(flags.get("b"));
        assertTrue(flags.get("c"));
    }

    @Test
    public void processOnlyOptionsWithSingleParameter() {
        var parser = new CmdLineParser();
        var configBuilder = new PaintSettings.Builder();

        parser.registerWithParameter(
            "-border-width",
            (arg) -> configBuilder.withBorders(Integer.parseInt(arg))
        );
        parser.registerWithParameter(
            "-window-name",
            configBuilder::withName
        );

        String[] args = {"-border-width", "10", "-window-name", "test"};
        parser.process(args);
        var config = configBuilder.build();
        assertEquals(10, config.getBorderWidth());
        assertEquals("test", config.getName());
    }

    @Test
    public void processOnlyOptionsWithSeveralParameters() {
        var parser = new CmdLineParser();
        var configBuilder = new PaintSettings.Builder();
        configBuilder.withName("test");

        parser.registerWithParameters(
                "-min-size",
                2,
                (args) -> configBuilder.withSize(Integer.parseInt(args.get(0)), Integer.parseInt(args.get(1)))
        );
        parser.registerWithParameters(
                "-remote-server",
                2,
                (args) -> configBuilder.withRemoteServer(args.get(0), Integer.parseInt(args.get(1)))
        );

        String[] args = {"-min-size", "10", "20", "-remote-server", "localhost", "8080"};
        parser.process(args);
        var config = configBuilder.build();
        assertEquals(10, config.getWidth());
        assertEquals(20, config.getHeight());
        assertTrue(config.getRemoteServer().isPresent());
    }

    @Test
    public void processAllOptions() {
        var parser = new CmdLineParser();
        var flags = new HashMap<>(Map.of(
            "a", false,
            "b", false,
            "c", false
        ));
        var configBuilder = new PaintSettings.Builder();

        parser.registerWithParameter(
                "-border-width",
                (arg) -> configBuilder.withBorders(Integer.parseInt(arg))
        );
        parser.registerWithParameter(
                "-window-name",
                configBuilder::withName
        );
        parser.registerFlag("-a", () -> flags.put("a", true));
        parser.registerFlag("-b", () -> flags.put("b", true));
        parser.registerFlag("-c", () -> flags.put("c", true));

        String[] args = {"-b", "aaa", "-c", "-border-width", "10", "-window-name", "test", "bbb"};
        var unregistered = parser.process(args);
        parser.process(args);
        var config = configBuilder.build();
        assertFalse(flags.get("a"));
        assertTrue(flags.get("b"));
        assertTrue(flags.get("c"));
        assertTrue(unregistered.containsAll(List.of("aaa", "bbb")));
        assertEquals(10, config.getBorderWidth());
        assertEquals("test", config.getName());
    }

    @Test
    public void unregisteredOption() {
        var parser = new CmdLineParser();
        String[] args = { "-a" };
        assertThrows(IllegalArgumentException.class, () -> parser.process(args));
    }

    @Test
    public void missingParameter() {
        var parser = new CmdLineParser();
        parser.registerWithParameter(
                "-a",
                (ignored) -> {}
        );
        parser.registerFlag("-b", () -> {});

        String[] args = { "-a", "-b" };
        assertThrows(IllegalArgumentException.class, () -> parser.process(args));
    }
}