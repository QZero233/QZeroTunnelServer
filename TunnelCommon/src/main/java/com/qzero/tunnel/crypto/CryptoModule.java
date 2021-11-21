package com.qzero.tunnel.crypto;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface CryptoModule {

    DataWithLength encrypt(DataWithLength data);

    DataWithLength decrypt(DataWithLength data);

    /**
     * Specify how many bytes of data are contained in one whole data package
     * If it's 0, it means any length is acceptable
     * @return
     */
    int getUnitPackageLength();

    default void doHandshakeAsServer(InputStream inputStream, OutputStream outputStream) throws IOException{

    }

    default void doHandshakeAsClient(InputStream inputStream, OutputStream outputStream) throws IOException{

    }

}
