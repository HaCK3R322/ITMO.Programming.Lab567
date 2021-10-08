package com.androsov.server.commands.pcommands;

import com.androsov.general.request.Request;
import com.androsov.general.response.Response;
import com.androsov.general.response.ResponseImpl;
import com.androsov.server.commands.Command;
import com.androsov.server.commands.CommandHandler;
import com.androsov.server.commands.CommandImpl;

import java.util.List;
import java.util.Map;

public class Help extends CommandImpl {
    private final CommandHandler commandHandler;

    public Help(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;

        name = "help";
        description = "If used without an argument, returns a list of commands with a description, otherwise displays help for this command";
        argumentFormat = "String|void";
        userAccessible = true;
    }

    @Override
    public Response execute(Request request) {
        List<Object> args = request.getArgs();
        Response response = new ResponseImpl(request.getUser());

        StringBuilder sb = new StringBuilder();
        if(args.size() != 0) {
            for (Object arg: args) {
                sb.append((String) arg);
                sb.append(": ");
                sb.append(commandHandler.getCommand((String) arg).getDescription());
                sb.append("\n");
            }
            sb.deleteCharAt(sb.length() - 1);
        } else {
            //TODO поправить смену языка
            sb.append("list of languages: ").append("RU ENG").append("\n\n");
            for (Map.Entry<String, Command> commandEntry : commandHandler.commandMap.entrySet()) {
                if (commandEntry.getValue().isUserAccessible()) {
                    sb.append(commandEntry.getKey());
                    sb.append(": ");
                    sb.append(commandEntry.getValue().getDescription());
                    sb.append("\n");
                }
            }
        }
        response.setMessage(sb.toString());

        return response;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
