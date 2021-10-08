package com.androsov.server.commands.pcommands;

import com.androsov.general.request.Request;
import com.androsov.general.response.Response;
import com.androsov.general.response.ResponseImpl;
import com.androsov.server.commands.CommandImpl;

import java.util.List;

public class ChangeLanguage extends CommandImpl {

    public ChangeLanguage() {

        name = "change_language";
        description = "changes language.";
        argumentFormat = "String";
        userAccessible = true;
    }

    public Response execute(Request request) {
        List<Object> args = request.getArgs();
        Response response = new ResponseImpl(request.getUser());

        if(args.size() > 0) {
            response.setMessage("language was changed to" + args.get(0));
        } else
            response.setMessage("Please, enter language");

        return response;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
