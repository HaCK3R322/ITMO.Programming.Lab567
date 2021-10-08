package com.androsov.client.ui.console;

import com.androsov.client.commands.CommandValidator;
import com.androsov.client.messenger.EngMessenger;
import com.androsov.client.messenger.Messenger;
import com.androsov.client.messenger.RuMessenger;
import com.androsov.client.ui.Ui;
import com.androsov.general.CommandFormatter;
import com.androsov.general.IO.IO;
import com.androsov.general.ObjectSerialization;
import com.androsov.general.User;
import com.androsov.general.request.Request;
import com.androsov.general.request.RequestImpl;
import com.androsov.general.response.Response;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Scanner;

public class CommandLineInterface implements Ui {
    private final User user;

    private final IO io;
    private final Scanner scanner = new Scanner(System.in);
    private final CommandValidator validator;
    private Messenger messenger;

    public CommandLineInterface(IO io, Messenger messenger, User user) throws IOException {
        this.user = user;
        this.io = io;
        this.messenger = messenger;
        

        io.send(ObjectSerialization.serialize(new RequestImpl("get_commands_formats", user)));
        validator = new CommandValidator(((Response) ObjectSerialization.deserialize(io.get())).getMessage());
    }

    /**
     * запрашивает адрес сервера у пользователя
     *
     * @return address
     */
    public static String askAddress() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter server ip:");
        String serverIp = sc.nextLine();
        if (serverIp.split(":").length != 2) {
            System.out.println("Wrong address format! Address format: <ip>:<port>");
            return askAddress();
        }

        return serverIp;
    }

    @Override
    public boolean userRegistered() throws IOException {
        while (true) {
            System.out.println("If you want to register, enter \"reg\"; If you want to login, enter \"log\";");
            System.out.print(">>> ");
            String command = getCommand();

            if (command.equals("exit")) {
                return false;
            }

            if (command.equals("log")) {
                System.out.println("Enter nickname: ");
                final String nickname = getCommand();
                System.out.println("Enter password (if you don't have, one just press enter): ");
                final String password = getCommand();
                user.setNickname(nickname); user.setPassword(toMD2(password));

                io.send(ObjectSerialization.serialize(new RequestImpl("login", user)));
                final Response response = (Response) ObjectSerialization.deserialize(io.get());
                System.out.println(response.getMessage());
                if(response.getData().get(0).equals(true)) {
                    return true;
                }
            } else if (command.equals("reg")) {
                System.out.println("Enter nickname: ");
                final String nickname = getCommand();
                System.out.println("Enter password (if you don't want to set password, just press enter): ");
                final String password = getCommand();
                user.setNickname(nickname); user.setPassword(toMD2(password));

                io.send(ObjectSerialization.serialize(new RequestImpl("regist", user)));
                final Response response = (Response) ObjectSerialization.deserialize(io.get());
                System.out.println(response.getMessage());
            }
        }
    }

    private String toMD2(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD2");
            byte[] messageDigest = md.digest(password.getBytes(StandardCharsets.UTF_8));
            BigInteger no = new BigInteger(1, messageDigest);
            StringBuilder hashtext = new StringBuilder(no.toString(16));
            while (hashtext.length() < 32) {
                hashtext.insert(0, "0");
            }

            return hashtext.toString();
        }

        // For specifying wrong message digest algorithms
        catch (Exception e) {
            System.out.println(e.getMessage());
            return "PASSWORD_HASH_ERROR";
        }
    }

    @Override
    public boolean endSession() {
        System.out.println("Do you really wanna exit? (type yes|y  to to end program or type any other key to continue work)");
        String answer = scanner.nextLine();
        return answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes");
    }

    @Override
    public String getCommand() { return scanner.nextLine();  }

    @Override
    public void sendResponse(String str) { System.out.println(str); }

    @Override
    public boolean askReconnect() {
        System.out.println("Do you wanna reconnect? (type yes|y or type any other key to end program)");
        String answer = scanner.nextLine();
        return (answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes"));
    }

    /**
     * основной цикл запросов и ответов на сервер и от сервера
     *
     * @throws IOException If some I/O exception occurred
     */
    @Override
    public void init() throws IOException {
        boolean userRegistered = userRegistered();
        System.out.println("reg cycle ended");

        io.send(ObjectSerialization.serialize(new RequestImpl("help", user)));
        System.out.println(((Response) ObjectSerialization.deserialize(io.get())).getMessage());
        System.out.println("-------------------------------------------------");
        System.out.println("Type command here:");

        if (userRegistered) {
            while (true) {

                //get new user command from System.in
                System.out.print(">>> ");
                String command = getCommand();

                if (command.equals("exit")) {
                    break;
                }

                //if command not void
                if (CommandFormatter.getLength(command) != 0) {
                    if (validator.isValid(command)) {
                        final String name = CommandFormatter.extractName(command);
                        final List<Object> args = CommandFormatter.extractArgs(command);

                        final Request request = new RequestImpl(name, args, user);

                        io.send(ObjectSerialization.serialize(request));
                        final Response response = (Response) ObjectSerialization.deserialize(io.get());

                        if (response.getMessage() != null)
                            System.out.println(response.getMessage());

                        if (response.getData().size() > 0)
                            for (Object data : response.getData())
                                System.out.println(data.toString());

                    } else {
                        System.out.println(messenger.Wrong_command_or_command_format_try_again());
                        continue;
                    }

                    if (CommandFormatter.extractName(command).equals("change_language")) {
                        if (command.split(" ").length == 2) {
                            messenger = changeLanguage(command.split(" ")[1]);
                        }
                    }
                }
            }
        }
    }

    /**
     * Staraya huita nado ismenit'
     *
     * @param language ru or eng or smthng
     * @return {@link Messenger}
     */
    public Messenger changeLanguage(String language) {
        String lang = language.toLowerCase();
        Messenger newMessenger;

        if(lang.equals("ru"))
            newMessenger = new RuMessenger();
        else
            newMessenger = new EngMessenger();

        return newMessenger;
    }
}