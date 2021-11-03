package fr.uge.poo.cmdline.ex2;

import org.junit.jupiter.api.Test;

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
        var config = new Config();

        parser.registerWithParameter(
            "-border-width",
            (arg) -> config.setBorderWidth(Integer.parseInt(arg))
        );
        parser.registerWithParameter(
            "-window-name",
            config::setWindowName
        );

        String[] args = {"-border-width", "10", "-window-name", "test"};
        parser.process(args);
        assertEquals(10, config.getBorderWidth());
        assertEquals("test", config.getWindowName());
    }

    @Test
    public void processAllOptions() {
        var parser = new CmdLineParser();
        var flags = new HashMap<>(Map.of(
            "a", false,
            "b", false,
            "c", false
        ));
        var config = new Config();

        parser.registerWithParameter(
            "-border-width",
            (arg) -> config.setBorderWidth(Integer.parseInt(arg))
        );
        parser.registerWithParameter(
            "-window-name",
            config::setWindowName
        );
        parser.registerFlag("-a", () -> flags.put("a", true));
        parser.registerFlag("-b", () -> flags.put("b", true));
        parser.registerFlag("-c", () -> flags.put("c", true));

        String[] args0 = {"-b", "aaa", "-c", "-border-width", "10", "-window-name", "test", "bbb"};
        var unregistered = parser.process(args0);
        assertFalse(flags.get("a"));
        assertTrue(flags.get("b"));
        assertTrue(flags.get("c"));
        assertTrue(unregistered.containsAll(List.of("aaa", "bbb")));
        assertEquals(10, config.getBorderWidth());
        assertEquals("test", config.getWindowName());
    }

    static class Config {
        private int borderWidth = 0;
        private String windowName = "";

        public int getBorderWidth() {
            return borderWidth;
        }

        public void setBorderWidth(int borderWidth) {
            this.borderWidth = borderWidth;
        }

        public String getWindowName() {
            return windowName;
        }

        public void setWindowName(String windowName) {
            this.windowName = windowName;
        }
    }
}