package com.androsov.general.request;

import com.androsov.general.User;

import java.io.Serializable;
import java.util.List;

/**
 * Simple Request interface, that can be serializable
 * <p>
 * contains {@link User}, {@link String} command name and {@code List<Object> that represents arguments for that command}
 */
public interface Request extends Serializable { // == command
    User getUser();
    String getCommandName();
    List<Object> getArgs();
}
