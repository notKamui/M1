package fr.uge.poo.cmdline.ex5;

import java.nio.file.Path;
import java.util.List;

public class Application {
    public static void main(String[] args) {
        String[] arguments = { "-name", "test", "-legacy", "-nb", "-aa", "filename1", "filename2" };

        var cmdParser = new CmdLineParser();
        var settingsBuilder = new PaintSettings.Builder();
//        cmdParser.registerWithParameter(
//                "-name",
//                settingsBuilder::withName
//        );
//        cmdParser.registerFlag("-legacy", settingsBuilder::legacy);
//        cmdParser.registerWithParameter(
//                "-with-borders",
//                (width) -> settingsBuilder.withBorders(Integer.parseInt(width))
//        );
//        cmdParser.registerFlag("-no-borders", settingsBuilder::withoutBorders);

        cmdParser.registerOption(new Option.SimpleOption(
                "-name", "set the name",
                false,
                settingsBuilder::withName
        ));

        cmdParser.registerOption(new Option.Flag(
                "-legacy", "set the legacy flag on",
                false,
                settingsBuilder::legacy
        ));

        cmdParser.registerOption(new Option.SimpleOption(
                "-with-borders", "set the border flag on and sets the width",
                false,
                width -> settingsBuilder.withBorders(Integer.parseInt(width))
        ));

        cmdParser.registerOption(new Option.Flag(
                List.of("-no-borders", "-nb"), "set the border flag off",
                false,
                settingsBuilder::withoutBorders
        ));

        cmdParser.registerOption(new Option.ComplexOption(
                "-aa", "yep",
                2,
                true,
                argv -> System.out.println(argv.get(0) + argv.get(1))
        ));

        var result = cmdParser.process(arguments);
        var settings = settingsBuilder.build();
        var files = result.stream().map(Path::of).toList();

        files.forEach(System.out::println);
        System.out.println(settings);
        cmdParser.usage();
    }
}
