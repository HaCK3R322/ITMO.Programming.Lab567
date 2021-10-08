package com.androsov.client.ui;

import java.io.IOException;

public interface Ui {
    String getCommand();
    void sendResponse(String line);
    boolean askReconnect();
    void init() throws IOException;
    boolean endSession();
    boolean userRegistered() throws IOException;
}
