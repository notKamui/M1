package fr.uge.poo.cmdline.ex6;

import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public void processOnlyUnregistered() throws ParseException {
        var parser = new CmdLineParser();
        String[] args = {"aaa", "bbb", "ccc"};
        assertTrue(parser.process(args).containsAll(List.of("aaa", "bbb", "ccc")));
    }

    @Test
    public void processOnlyFlags() throws ParseException {
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
    public void processOnlyOptionsWithSingleParameter() throws ParseException {
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
    public void processOnlyOptionsWithSeveralParameters() throws ParseException {
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
    public void processAllOptions() throws ParseException {
        var parser = new CmdLineParser();
        var configBuilder = new PaintSettings.Builder();

        parser.registerWithParameter(
            "-border-width",
            (arg) -> configBuilder.withBorders(Integer.parseInt(arg))
        );
        parser.registerWithParameter(
            "-window-name",
            true,
            configBuilder::withName
        );
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
        parser.registerFlag("-legacy", configBuilder::legacy);
        parser.registerFlag("-no-borders", configBuilder::withoutBorders);

        String[] args = {"-legacy", "aaa", "-border-width", "10", "-window-name", "test", "bbb"};
        var unregistered = parser.process(args);
        parser.process(args);
        var config = configBuilder.build();
        assertTrue(config.isLegacy());
        assertTrue(config.hasBorders());
        assertTrue(unregistered.containsAll(List.of("aaa", "bbb")));
        assertEquals(10, config.getBorderWidth());
        assertEquals("test", config.getName());
    }

    @Test
    public void unregisteredOption() {
        var parser = new CmdLineParser();
        String[] args = {"-a"};
        assertThrows(ParseException.class, () -> parser.process(args));
    }

    @Test
    public void missingParameter() {
        var parser = new CmdLineParser();
        parser.registerWithParameter(
            "-a",
            (ignored) -> {}
        );
        parser.registerFlag("-b", () -> {});

        String[] args = {"-a", "-b"};
        assertThrows(ParseException.class, () -> parser.process(args));
    }

    @Test
    public void missingRequiredParameter() {
        var parser = new CmdLineParser();
        parser.registerFlag("-a", true, () -> {
        });
        String[] args = {"aaa"};
        assertThrows(ParseException.class, () -> parser.process(args));
    }

    @Test
    public void processRequiredOption() {
        var cmdParser = new CmdLineParser();
        var option = new Option.Builder().addName("-test").withProcess(0, l -> {
        }).isRequired(true).build();
        cmdParser.registerOption(option);
        cmdParser.registerFlag("-test1", () -> {
        });
        String[] arguments = {"-test1", "a", "b"};
        assertThrows(ParseException.class, () -> {
            cmdParser.process(arguments);
        });
    }

    @Test
    public void processConflicts() {
        var cmdParser = new CmdLineParser();
        var option = new Option.Builder().addName("-test").withProcess(0, l -> {
        }).addConflict("-test1").build();
        cmdParser.registerOption(option);
        var option2 = new Option.Builder().addName("-test1").withProcess(0, l -> {
        }).build();
        cmdParser.registerOption(option2);
        String[] arguments = {"-test", "-test1"};
        assertThrows(ParseException.class, () -> {
            cmdParser.process(arguments);
        });
    }

    @Test
    public void processConflicts2() {
        var cmdParser = new CmdLineParser();
        var option = new Option.Builder().addName("-test").withProcess(0, l -> {
        }).addConflict("-test1").build();
        cmdParser.registerOption(option);
        var option2 = new Option.Builder().addName("-test1").withProcess(0, l -> {
        }).build();
        cmdParser.registerOption(option2);
        String[] arguments = {"-test1", "-test"};
        assertThrows(ParseException.class, () -> {
            cmdParser.process(arguments);
        });
    }

    @Test
    public void processConflictsAndAliases() {
        var cmdParser = new CmdLineParser();
        var option = new Option.Builder().addName("-test").withProcess(0, l -> {
        }).addConflict("-test2").build();
        cmdParser.registerOption(option);
        var option2 = new Option.Builder().addName("-test1").withProcess(0, l -> {
        }).addName("-test2").build();
        cmdParser.registerOption(option2);
        String[] arguments = {"-test1", "-test"};
        assertThrows(ParseException.class, () -> {
            cmdParser.process(arguments);
        });
    }

    @Test
    public void processConflictsAndAliases2() {
        var cmdParser = new CmdLineParser();
        var option = new Option.Builder().addName("-test").withProcess(0, l -> {
        }).addConflict("-test2").build();
        cmdParser.registerOption(option);
        var option2 = new Option.Builder().addName("-test1").withProcess(0, l -> {
        }).addName("-test2").build();
        cmdParser.registerOption(option2);
        String[] arguments = {"-test", "-test1"};
        assertThrows(ParseException.class, () -> {
            cmdParser.process(arguments);
        });
    }
}
