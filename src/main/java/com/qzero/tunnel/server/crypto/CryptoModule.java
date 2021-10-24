package com.qzero.tunnel.server.crypto;

public interface CryptoModule<T extends CryptoContext> {

    byte[] encrypt(byte[] data,T context);

    byte[] decrypt(byte[] data,T context);

    CryptoContext getInitialContext();
}
