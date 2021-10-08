package com.androsov.server.commands.pcommands;

import com.androsov.general.request.Request;
import com.androsov.general.response.Response;
import com.androsov.general.response.ResponseImpl;
import com.androsov.server.commands.CommandHandler;
import com.androsov.server.commands.CommandImpl;
import com.androsov.server.localization.Messenger;

import java.util.List;

public class History extends CommandImpl {
    private final CommandHandler commandHandler;

    public History(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;

        name = "history";
        description = "Shows 12 last used commands.";
        argumentFormat = "void";
        userAccessible = true;
    }

    @Override
    public Response execute(Request request) {
        List<Object> args = request.getArgs();
        Response response = new ResponseImpl(request.getUser());

        StringBuilder result = new StringBuilder(Messenger.rb.getString("history.history") + ":\n");

        int NUMBER_OF_LAST_COMMANDS_TO_SHOW = 12;

        int historySize = commandHandler.history.size();
        List<String> lastCommands = commandHandler.history.subList(
                historySize - Math.min(historySize, NUMBER_OF_LAST_COMMANDS_TO_SHOW), historySize
        );

        for (String lastCommand : lastCommands) {
            result.append("   - ").append(lastCommand).append("\n");
        }

        response.setMessage(result.toString());

        return response;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
