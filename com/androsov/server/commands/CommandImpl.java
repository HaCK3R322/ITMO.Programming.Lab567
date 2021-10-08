package com.androsov.server.commands;

/**
 * Variation of {@link Command} implementation.
 */
public abstract class CommandImpl implements Command {
    public String name;
    public String description;
    public String argumentFormat;
    public boolean userAccessible;

    public String getName() {
        return name;
    }
    public abstract String getDescription();
    public boolean isUserAccessible() { return userAccessible; }
    public String getArgumentFormat() { return argumentFormat; }
}
