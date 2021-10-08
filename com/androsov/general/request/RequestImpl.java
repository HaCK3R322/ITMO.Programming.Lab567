package com.androsov.general.request;

import com.androsov.general.User;

import java.util.LinkedList;
import java.util.List;

/**
 * Variation of {@link Request} implementation.
 */
public class RequestImpl implements Request {
    private final User user;
    private final String commandName;
    private final List<Object> argsList;

    public RequestImpl(String commandName, User user) {
        this.user = user;
        this.commandName = commandName;
        argsList = new LinkedList<>();
    }

    public RequestImpl(String commandName, List<Object> argsList, User user) {
        this.user = user;
        this.commandName = commandName;
        this.argsList = argsList;
    }

    @Override
    public String getCommandName() {
        return commandName;
    }

    @Override
    public List<Object> getArgs() {
        return argsList;
    }

    @Override
    public User getUser() { return user; }
}
