package com.androsov.client.commands;

import com.androsov.general.CommandFormatter;

import java.util.HashMap;
import java.util.LinkedList;

public class CommandValidator {
    public static HashMap<String, Class> toClassMap;
    private final HashMap<String, LinkedList<Class>> commandsArguments;


    /**
     * Создаёт на основе ответа от сервера набор команд и аргументов, которые можно использовать с этими командами
     * @param commandsArguments
     */
    public CommandValidator(String commandsArguments) { // переделать под чтение респонса
        this.commandsArguments = new HashMap<>();

        toClassMap = new HashMap<>();
        toClassMap.put("void", null);
        toClassMap.put("String", String.class);

        toClassMap.put("Integer", Long.class);
        toClassMap.put("Long", Long.class);

        toClassMap.put("Double", Double.class);
        toClassMap.put("Float", Double.class);

        String[] arrayOfCommands = commandsArguments.split("\n");

        for (String arrayOfCommand : arrayOfCommands) {
            String commandName = arrayOfCommand.split(" ")[0];
            String[] commandArguments = arrayOfCommand.split(" ")[1].split("\\|");
            try {
                LinkedList<Class> commandArgumentsClassRepresentation = new LinkedList<>(); // создаем лист для перевода из String формы в Class
                for (String commandArgument : commandArguments) {
                    commandArgumentsClassRepresentation.add(toClassMap.get(commandArgument)); // добавляем
                }
                this.commandsArguments.put(commandName, commandArgumentsClassRepresentation); // запихиваем в мапу
            } catch (NullPointerException e) {
                System.out.println("Command wrong argument on command " + commandName);
            }
        }
    }

    /**
     * смотрит есть ли такая команда в доступных пользователю, затем определяет тип её аргумента и ищет его в списке доступных аргуменотов для этой команды
     * @param commandLine
     * @return
     */
    public boolean isValid(String commandLine) {
        String commandName = commandLine.split(" ")[0];
        String commandArgument = commandLine.split(" ").length > 1 ? commandLine.split(" ")[1] : "";

        if(commandsArguments.containsKey(commandName)) {
            boolean argumentValid = false;

            for(int i = 0; i < commandsArguments.get(commandName).size(); i++) {
                if(commandsArguments.get(commandName).get(i) == CommandFormatter.getClass(commandArgument))
                    argumentValid = true;
            }

            return argumentValid;
        } else {
            return false;
        }
    }
}
