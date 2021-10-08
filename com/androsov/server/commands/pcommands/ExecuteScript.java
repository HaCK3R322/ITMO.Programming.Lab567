package com.androsov.server.commands.pcommands;

import com.androsov.general.CommandFormatter;
import com.androsov.general.request.Request;
import com.androsov.general.request.RequestImpl;
import com.androsov.general.response.Response;
import com.androsov.general.response.ResponseImpl;
import com.androsov.server.commands.CommandHandler;
import com.androsov.server.commands.CommandImpl;
import com.androsov.server.localization.Messenger;
import com.androsov.server.products.exceptions.SelfCycledScriptChainException;
import com.androsov.server.scripting.Script;

import java.io.IOException;
import java.util.List;

public class ExecuteScript extends CommandImpl {
    private final CommandHandler commandHandler;

    public ExecuteScript(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;

        name = "execute_script";
        description = "executes script. Command format: execute_script <path to script>.";
        argumentFormat = "String";
        userAccessible = true;
    }

    public Response execute(Request request) {
        List<Object> args = request.getArgs();
        Response response = new ResponseImpl(request.getUser());
        StringBuilder result = new StringBuilder();

        try {
            String scriptName = (String) args.get(0);
            Script script = new Script(scriptName);

            for (String commandLine : script.commands) {
                final Request requestFromScript = new RequestImpl(CommandFormatter.extractName(commandLine), CommandFormatter.extractArgs(commandLine), request.getUser());
                result.append(Messenger.rb.getString("executeScript.script")).append(": ").append(commandHandler.executeCommand(requestFromScript)).append('\n');
            }

            result.append("<").append(Messenger.rb.getString("executeScript.script")).append(scriptName).append(Messenger.rb.getString("executeScript.executed")).append(".>");

        } catch (NullPointerException e) {
            result = new StringBuilder(Messenger.rb.getString("executeScript.Please_enter_script_name"));
        } catch (IOException | SelfCycledScriptChainException e) {
            result = new StringBuilder("<" + Messenger.rb.getString("executeScript.Script_error") + ">: " + e.getMessage());
        }

        response.setMessage(result.toString());
        return response;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
