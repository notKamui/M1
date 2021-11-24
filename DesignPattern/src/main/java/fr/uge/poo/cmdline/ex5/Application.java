package fr.uge.poo.cmdline.ex5;

import java.nio.file.Path;

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

        cmdParser.registerOption(new Option.Builder()
            .addName("-name")
            .withDoc("set the name")
            .withStringProcess(settingsBuilder::withName)
            .build());

        cmdParser.registerOption(new Option.Builder()
            .addName("-legacy")
            .withDoc("set the legacy flag on")
            .withFlag(settingsBuilder::legacy)
            .build());

        cmdParser.registerOption(new Option.Builder()
            .addName("-with-borders")
            .withDoc("set the border flag on and sets the width")
            .withIntProcess(settingsBuilder::withBorders)
            .build());

        cmdParser.registerOption(new Option.Builder()
            .addName("-no-borders")
            .addName("-nb")
            .withDoc("set the border flag off")
            .withFlag(settingsBuilder::withoutBorders)
            .build());

        cmdParser.registerOption(new Option.Builder()
            .addName("-aa")
            .withDoc("yep")
            .isRequired(true)
            .withProcess(2, argv -> System.out.println(argv.get(0) + argv.get(1)))
            .build());

        var result = cmdParser.process(arguments);
        var settings = settingsBuilder.build();
        var files = result.stream().map(Path::of).toList();

        files.forEach(System.out::println);
        System.out.println(settings);
        cmdParser.usage();
    }
}
