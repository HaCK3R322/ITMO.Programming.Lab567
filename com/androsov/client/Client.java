package com.androsov.client;

import com.androsov.client.io.ClientIO;
import com.androsov.client.messenger.EngMessenger;
import com.androsov.client.messenger.Messenger;
import com.androsov.client.ui.Ui;
import com.androsov.client.ui.console.CommandLineInterface;
import com.androsov.general.IO.IO;
import com.androsov.general.User;

import java.io.IOException;

//вариант 296173
public class Client {
    public static void main(String[] args) {
        Messenger messenger = new EngMessenger();

        User user;

        ClientIO clientIO = new ClientIO();
        Ui ui;

        boolean firstInit = true;
        String serverAddress = "localhost:25565";
        while (true) {
            if(!firstInit)
                serverAddress = CommandLineInterface.askAddress();
            firstInit = false;

            try {
                String serverIp = serverAddress.split(":")[0];
                int serverPort;
                try {
                    serverPort = Integer.parseInt(serverAddress.split(":")[1]);
                } catch (NumberFormatException e) {
                    continue;
                }

                try {
                    clientIO.connectToServer(serverIp, serverPort);
                } catch (IllegalArgumentException e) {
                    System.out.println("Incorrect address: " + e.getMessage() + "\nTry again.");
                    continue;
                }

                user = new User(clientIO.getLocalAddress());

                ui = new CommandLineInterface(clientIO, messenger, user);

                ui.init();
                if(!ui.askReconnect()) {
                    break;
                }
            } catch (IOException e) {
                System.out.println("Server connection error: " + e.getLocalizedMessage());
            }
        }
    }
}
