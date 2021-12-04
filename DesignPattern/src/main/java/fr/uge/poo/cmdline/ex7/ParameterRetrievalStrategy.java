package fr.uge.poo.cmdline.ex7;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to retrieve the parameters from the command line.
 */
@FunctionalInterface
public interface ParameterRetrievalStrategy {
    /**
     * Standard strategy to retrieve the parameters.
     */
    ParameterRetrievalStrategy STANDARD = (args, option, actualName) -> {
        var params = new ArrayList<String>();
        for (var i = 0; i < option.arity(); i++) {
            if (!args.hasNext()) throw missingParameter(actualName);
            var param = args.next();
            if (startsWithDash(param)) throw missingParameter(actualName);
            params.add(param);
        }
        return params;
    };
    /**
     * Relaxed strategy to retrieve the parameters.
     */
    ParameterRetrievalStrategy RELAXED = (args, option, actualName) -> {
        var params = new ArrayList<String>();
        for (var i = 0; i < option.arity(); i++) {
            if (!args.hasNext()) break;
            if (startsWithDash(args.peek())) break;
            params.add(args.next());
        }
        return params;
    };
    /**
     * Oldschool strategy to retrieve the parameters.
     */
    ParameterRetrievalStrategy OLDSCHOOL = (args, option, actualName) -> {
        var params = new ArrayList<String>();
        for (var i = 0; i < option.arity(); i++) {
            if (!args.hasNext()) throw missingParameter(actualName);
            params.add(args.next());
        }
        return params;
    };

    private static ParseException missingParameter(String option) {
        return new ParseException("Missing required option : " + option, 0);
    }

    private static boolean startsWithDash(String s) {
        return s.charAt(0) == '-';
    }

    List<String> retrieveParameter(PeekIterator<String> args, Option option, String actualName) throws ParseException;
}
