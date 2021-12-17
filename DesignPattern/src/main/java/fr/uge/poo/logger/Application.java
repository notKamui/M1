package fr.uge.poo.logger;

import com.evilcorp.logger.ComposedLogger;
import com.evilcorp.logger.FilteredLogger;
import com.evilcorp.logger.Logger;
import com.evilcorp.logger.PathLogger;
import com.evilcorp.logger.SystemLogger;

import java.nio.file.Path;

public class Application {

    public static void main(String[] args) {
        try (var pathLogger = new PathLogger(Path.of("tmp", "logs.txt"))) {
            var logger = new ComposedLogger(
                new FilteredLogger(pathLogger, level -> level.ordinal() <= Logger.Level.WARNING.ordinal()),
                SystemLogger.instance()
            );
            logger.log(Logger.Level.INFO, "Hello world");
            logger.log(Logger.Level.WARNING, "Watch out!");
            logger.log(Logger.Level.ERROR, "Oh no!");
        }
    }
}
