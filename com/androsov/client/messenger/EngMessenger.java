package com.androsov.client.messenger;

public class EngMessenger implements Messenger {
    @Override
    public String Try_again() {
        return "Try again";
    }

    @Override
    public String Cannot_connect_to_server() {
        return "Cannot connect to server";
    }

    @Override
    public String Wrong_port_format() {
        return "Wrong port format";
    }

    @Override
    public String Some_problems_with_server() {
        return "Some problems with server";
    }

    @Override
    public String Do_you_wanna_try_to_reconnect() {
        return "Do you wanna try to reconnect";
    }

    @Override
    public String Trying_to_get_access_to_server_opened_on() {
        return "Trying to get access to server opened on";
    }

    @Override
    public String voidSpace() {
        return "";
    }

    @Override
    public String Wrong_command_or_command_format_try_again() {
        return "Wrong command or command format, try again!";
    }
}
