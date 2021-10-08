package com.androsov.server.commands.pcommands;

import com.androsov.general.request.Request;
import com.androsov.general.response.Response;
import com.androsov.general.response.ResponseImpl;
import com.androsov.server.commands.CommandImpl;

import java.util.List;

public class Exit extends CommandImpl {
    public Exit() {
        name = "exit";
        description = "ends session.";
        argumentFormat = "void";
        userAccessible = true;
    }

    @Override
    public Response execute(Request request) {
        List<Object> args = request.getArgs();
        Response response = new ResponseImpl(request.getUser());
        response.setMessage("\0");
        return response;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
