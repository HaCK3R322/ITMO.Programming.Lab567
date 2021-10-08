package com.androsov.server.commands;

import com.androsov.server.products.exceptions.CommandBuildError;

import java.lang.reflect.Field;

/**
 * Checks if all of {@link Command} fields was assigned.
 */
public class CommandStructureChecker {
    public static void check(Command command) throws CommandBuildError {
        Class<?> commandClass = command.getClass();
        Class<?> current = command.getClass();
        while(current.getSuperclass() != null){
            for(Field field : current.getDeclaredFields()) {
                try {
                    if (field.get(command) == null) throw new CommandBuildError("In command " + commandClass.getName() + " field \"" + field.getName() + "\" is null!");
                } catch (IllegalAccessException ignored) {

                }
            }
            current = current.getSuperclass();
        }
    }
}
