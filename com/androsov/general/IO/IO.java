package com.androsov.general.IO;

import java.nio.ByteBuffer;

public interface IO {
    void send(ByteBuffer buffer);
    ByteBuffer get();
}
