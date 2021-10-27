package com.qzero.tunnel.server.crypto;

import com.qzero.tunnel.server.crypto.modules.PlainModule;

public class CryptoModuleFactory {

    public static CryptoModule getModule(String name){
        switch (name){
            case "plain":
                return new PlainModule();
            default:
                return null;
        }
    }

}
