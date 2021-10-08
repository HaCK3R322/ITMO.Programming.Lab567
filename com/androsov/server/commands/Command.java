package com.androsov.server.commands;

import com.androsov.general.request.Request;
import com.androsov.general.response.Response;

/**
 * Simple command, that executes, contains command name, description, argument format and user accessibility.
 */
public interface Command {
    Response execute(Request request);
    String getName();
    String getDescription();
    String getArgumentFormat();
    boolean isUserAccessible();
}
