package com.androsov.client.io;

import com.androsov.general.IO.IO;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

public class ClientIO implements IO, Closeable {
    private Socket socket;

    public void connectToServer(String serverIP, int serverPort) throws IOException {
        socket = new Socket(serverIP, serverPort);
        System.out.println(socket.getLocalSocketAddress());
    }

    @Override
    public void send(ByteBuffer buffer) {
        try {
            socket.getOutputStream().write(buffer.array());
        } catch (IOException e) {
            System.out.println("Client \"Send\" exception: " + e.getMessage());
        }
    }

    @Override
    public ByteBuffer get() {
        try {
            final ByteBuffer buffer = ByteBuffer.allocate(16384);
            socket.getInputStream().read(buffer.array());
            return buffer;
        } catch (IOException e) {
            System.out.println("Client \"Get\" exception: " + e.getMessage());
            return ByteBuffer.allocate(0);
        }
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }

    public SocketAddress getLocalAddress() {
        return socket.getLocalSocketAddress();
    }
}
