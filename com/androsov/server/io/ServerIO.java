package com.androsov.server.io;

import com.androsov.general.ObjectSerialization;
import com.androsov.general.request.Request;
import com.androsov.general.response.Response;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Simple ServerIO, that allows to get {@link Request} and send {@link Response}.
 * <p>
 * ServerIO consists of a ServerSocketChannel, a Selector, and several different methods to accept requests and send responses.
 */
public class ServerIO {
    private final ServerSocketChannel serverSocketChannel;
    public final Selector selector;

    /**
     * Constructs simple Server IO: opens {@link ServerSocketChannel}, binds it to {@link InetSocketAddress},
     * configures blocking to {@code false}, opens {@link Selector} and regist it;
     * @throws IOException If an I/O error occurs.
     */
    public ServerIO() throws IOException {
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress("localhost", 25565));
        serverSocketChannel.configureBlocking(false);

        selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    /**
     * Starts listening client channels by accepting them by {@link ServerSocketChannel}
     * @throws IOException If an I/O error occurs.
     * @throws CancelledKeyException If the client trying to connect has disconnected.
     */
    public void acceptAll() throws IOException, CancelledKeyException {
        try {
            selector.select();
        } catch (IOException ignored) {}

        final Set<SelectionKey> keys = selector.selectedKeys();
        final Iterator<SelectionKey> keyIterator = keys.iterator();

        for(int i = 0; i < keys.size(); i++) {
            try {
                final SelectionKey key = keyIterator.next();
                if (key.isAcceptable()) {
                    final SocketChannel clientSocketChannel = serverSocketChannel.accept();
                    clientSocketChannel.configureBlocking(false);
                    clientSocketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                    Logger.getLogger("LOGGER").info("Connected client " + clientSocketChannel.socket().getRemoteSocketAddress());
                    keyIterator.remove();
                }
            } catch (ConcurrentModificationException |  NoSuchElementException e) {
                Logger.getLogger("LOGGER").warning(e.getMessage());
            }
        }
    }

    /**
     * Checks if server has any request by checking if there is some readable key.
     * @return {@code boolean}
     */
    public boolean hasRequest() {
        final Set<SelectionKey> keys = selector.selectedKeys();
        final Iterator<SelectionKey> keyIterator = keys.iterator();

        for(int i = 0; i < keys.size(); i++) {
            try {
                final SelectionKey key = keyIterator.next();
                if (key.isReadable()) {
                    return true;
                }
            } catch (ConcurrentModificationException e) {
                Logger.getLogger("LOGGER").warning(e.getMessage());
                return false;
            } catch (NoSuchElementException ignored) {

            }
        }

        return false;
    }

    /**
     * Tries to send back {@link Response} to user that sent {@link Request} by comparing remote addresses
     * written in {@link Request} and remote addresses of keys.
     * <p>
     * If there is no writable key with such remote address, it can be compared to user disconnection.
     *
     * @param response {@link Response}
     * @throws CancelledKeyException In most cases, this means that we found someone to send a {@code response} to,
     *                               but at that moment the client disconnected.
     */
    public void send(Response response) throws CancelledKeyException {
        ByteBuffer buffer = ByteBuffer.allocate(0);
        try {
            buffer = ObjectSerialization.serialize(response);
        } catch (IOException e) {
            System.out.println();
        }

        try {
            selector.select();
        } catch (IOException ignored) {
        }
        final Set<SelectionKey> keys = selector.selectedKeys();
        final Iterator<SelectionKey> keyIterator = keys.iterator();

        for (int i = 0; i < keys.size(); i++) {
            final SelectionKey key = keyIterator.next();
            try {

                if (key.isWritable() && ((SocketChannel) key.channel()).getRemoteAddress().equals(response.getUser().getUserAddress())) {
                    ((SocketChannel) key.channel()).write(buffer);
                    keyIterator.remove();
                    return;
                }

            } catch (IOException e) {
                Logger.getLogger("LOGGER").warning(e.getMessage());
                try {
                    key.channel().close();
                    ((SocketChannel) key.channel()).socket().close();
                } catch (IOException ignored) { }
            }
        }
        Logger.getLogger("LOGGER").warning("No available channels at the moment");
    }

    /**
     * Returns {@link Request} from readable keys: iterates through all keys, if one of them is {@code readable},
     * reads the buffer, transforms bytes into a {@link Request}, and returns it.
     * <p>
     * <b>Note:</b> If there are no available requests, it returns {@code Request == null}.
     *
     * @return {@link Request}
     * @throws CancelledKeyException equals to disconnecting of client
     */
    public synchronized Request get() throws CancelledKeyException {
        try { selector.select(); } catch (IOException ignored) {}

        final ByteBuffer buffer = ByteBuffer.allocate(16384);
        Request request = null;

        final Set<SelectionKey> keys = selector.selectedKeys();
        final Iterator<SelectionKey> keyIterator = keys.iterator();

        for(int i = 0; i < keys.size(); i++) {
            final SelectionKey key = keyIterator.next();
            if (key.isReadable()) {
                try {
                    ((SocketChannel)key.channel()).read(buffer); // waits until buffer gets at least one byte
                    keyIterator.remove();

                    request = (Request) ObjectSerialization.deserialize(buffer);
                    break;
                } catch (IOException e) {
                    if (e.getMessage().equals("Connection reset")) {
                        Logger.getLogger(("LOGGER")).info(((SocketChannel) key.channel()).socket().getRemoteSocketAddress() + " disconnected");
                    } else {
                        Logger.getLogger(("LOGGER")).warning(e.getMessage());
                    }
                    try {
                        key.channel().close();
                        ((SocketChannel) key.channel()).socket().close();
                    } catch (IOException ignored) {}
                }
            }
        }

        return request;
    }
}
