package com.qzero.tunnel.crypto;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface CryptoModule {

    DataWithLength encrypt(DataWithLength data) throws CryptoException;

    DataWithLength decrypt(DataWithLength data) throws CryptoException;

    default void doHandshakeAsServer(InputStream inputStream, OutputStream outputStream) throws IOException{

    }

    default void doHandshakeAsClient(InputStream inputStream, OutputStream outputStream) throws IOException{

    }

}
