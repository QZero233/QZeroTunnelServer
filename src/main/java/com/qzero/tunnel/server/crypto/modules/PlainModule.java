package com.qzero.tunnel.server.crypto.modules;

import com.qzero.tunnel.server.crypto.CryptoContext;
import com.qzero.tunnel.server.crypto.CryptoModule;
import com.qzero.tunnel.server.crypto.CryptoModuleClass;

@CryptoModuleClass(name = "plain")
public class PlainModule implements CryptoModule<CryptoContext> {

    @Override
    public byte[] encrypt(byte[] data, CryptoContext context) {
        return data;
    }

    @Override
    public byte[] decrypt(byte[] data, CryptoContext context) {
        return data;
    }

    @Override
    public CryptoContext getInitialContext() {
        return new CryptoContext();
    }


}
