package fr.uge.poo.logger;

import com.evilcorp.logger.FilteredLogger;
import com.evilcorp.logger.Logger;
import com.evilcorp.logger.PathLogger;
import java.nio.file.Path;

public class Application {

    public static void main(String[] args) {
        var logger = new FilteredLogger(new PathLogger(Path.of("tmp", "logs.txt")), Logger.Level.WARNING);
        logger.log(Logger.Level.INFO, "Hello world");
        logger.log(Logger.Level.WARNING, "Watch out!");
        logger.log(Logger.Level.ERROR, "Oh no!");
    }
}
