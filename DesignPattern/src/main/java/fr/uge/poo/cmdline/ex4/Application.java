package fr.uge.poo.cmdline.ex4;

import java.nio.file.Path;

public class Application {
    public static void main(String[] args) {
        String[] arguments = { "-name", "test", "-legacy", "-no-borders", "filename1", "filename2" };

        var cmdParser = new CmdLineParser();
        var settingsBuilder = new PaintSettings.Builder();
        cmdParser.registerWithParameter(
                "-name",
                settingsBuilder::withName
        );
        cmdParser.registerFlag("-legacy", settingsBuilder::legacy);
        cmdParser.registerWithParameter(
                "-with-borders",
                (width) -> settingsBuilder.withBorders(Integer.parseInt(width))
        );
        cmdParser.registerFlag("-no-borders", settingsBuilder::withoutBorders);
        var result = cmdParser.process(arguments);
        var settings = settingsBuilder.build();
        var files = result.stream().map(Path::of).toList();

        files.forEach(System.out::println);
        System.out.println(settings);
    }
}
