package com.androsov.general;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

public class ObjectSerialization {
    /**
     * Serializes any {@link Object} to {@link ByteBuffer}
     * @param object Any {@link Object} object
     * @return {@link ByteBuffer} that contains serialized object
     * @throws IOException If any I/O exception occurred
     */
    public static ByteBuffer serialize(Object object) throws IOException {

        ByteBuffer buffer = ByteBuffer.allocate(16384);
        buffer.clear();

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(object);
            buffer.put(byteArrayOutputStream.toByteArray());
            buffer.position(0);
        }

        return buffer;
    }

    /**
     * Deserializes {@link ByteBuffer} to {@link Object}
     * @param buffer {@link ByteBuffer} that must contain serialized {@link Object}
     * @return {@link ByteBuffer} that contains serialized object
     * @throws IOException If any I/O exception occurred
     */
    public static Object deserialize(ByteBuffer buffer) throws IOException {
        Object object = null; //может быть поправить, может нет
        buffer.position(0);

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer.array());
             ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
            try {
                object = objectInputStream.readObject();
            } catch (ClassNotFoundException e) {
                Logger.getLogger(("LOGGER")).warning(e.getMessage());
            }
        }

        return object;
    }
}
