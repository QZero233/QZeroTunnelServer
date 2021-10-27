package com.qzero.tunnel.server.crypto;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface CryptoModule {

    byte[] encrypt(byte[] data);

    byte[] decrypt(byte[] data);

    default void doHandshakeAsServer(InputStream inputStream, OutputStream outputStream) throws IOException{

    }

    default void doHandshakeAsClient(InputStream inputStream, OutputStream outputStream) throws IOException{

    }
}
