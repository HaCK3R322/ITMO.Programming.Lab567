package com.androsov.client.messenger;

public class RuMessenger implements Messenger {
    @Override
    public String Try_again() {
        return "Попробуйте еще раз";
    }

    @Override
    public String Cannot_connect_to_server() {
        return "Невозможно подключиться к серверу";
    }

    @Override
    public String Wrong_port_format() {
        return "Неверный формат порта";
    }

    @Override
    public String Some_problems_with_server() {
        return "Ошибка подключения к серверу";
    }

    @Override
    public String Do_you_wanna_try_to_reconnect() {
        return "Вы хотите попробовать переподключиться";
    }

    @Override
    public String Trying_to_get_access_to_server_opened_on() {
        return "Попытка подключится к серверу, открытому по адресу";
    }

    @Override
    public String voidSpace() {
        return "";
    }

    @Override
    public String Wrong_command_or_command_format_try_again() {
        return "Неверная команада или формат команды, попробуйте еще!";
    }
}
