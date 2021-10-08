package com.androsov.general;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * Provides several static methods for extracting commands from strings.
 * With the transition to Request-Response, the style only needed for parsing scripts
 * @see com.androsov.server.scripting.Script
 */
public class CommandFormatter {
    public static int getLength(String commandLine) {
        return commandLine.split(" ").length;
    }

    public static String extractName(String commandLine) {
        return commandLine.split(" ")[0];
    }

    public static List<Object> extractArgs(String commandLine) {
        final List<Object> args = new LinkedList<>();

        String[] splattedCommandLine = commandLine.split(" ");

        int numberOfArgs = splattedCommandLine.length - 1;

        if (numberOfArgs > 0) {
            String[] argsStringFormat = Arrays.copyOfRange(splattedCommandLine, 1, splattedCommandLine.length);
            for (String s : argsStringFormat) {
                args.add(getObjectForm(s));
            }
        }

        return args;
    }

    public static Object getObjectForm(String arg) {
        Class<?> argClass = getClass(arg);
        if (argClass.equals(Long.class))
            return Long.parseLong(arg);
        else if (argClass.equals(Double.class))
            return Double.parseDouble(arg);
        else return arg;
    }

    public static Class<?> getClass(String value) {
        try(Scanner sc = new Scanner(value)) {
            if (sc.hasNextLong())
                return Long.class;
            else if (sc.hasNextDouble())
                return Double.class;
            else if(sc.hasNextLine())
                return String.class;
            else return null;
        }
    }
}
