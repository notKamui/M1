package fr.uge.poo.cmdline.ex2;

import java.nio.file.Path;

public class Application {

    private static class PaintSettings {
        private boolean isLegacy = false;
        private boolean isBordered = false;

        public boolean isLegacy() {
            return isLegacy;
        }

        public void setLegacy(boolean legacy) {
            isLegacy = legacy;
        }

        public boolean isBordered() {
            return isBordered;
        }

        public void setBordered(boolean bordered) {
            isBordered = bordered;
        }

        @Override
        public String toString() {
            return "PaintSettings{" +
                    "isLegacy=" + isLegacy +
                    ", isBordered=" + isBordered +
                    '}';
        }
    }

    public static void main(String[] args) {
        String[] arguments = { "-legacy", "-no-borders", "filename1", "filename2" };

        var cmdParser = new CmdLineParser();
        var settings = new PaintSettings();
        cmdParser.registerOption("-legacy", () -> settings.setLegacy(true));
        cmdParser.registerOption("-with-borders", () -> settings.setBordered(true));
        cmdParser.registerOption("-no-borders", () -> settings.setBordered(false));
        var result = cmdParser.process(arguments);
        var files = result.stream().map(Path::of).toList();

        files.forEach(System.out::println);
        System.out.println(settings);
    }
}
