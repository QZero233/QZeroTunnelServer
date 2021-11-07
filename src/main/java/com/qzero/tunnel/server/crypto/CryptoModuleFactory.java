package com.qzero.tunnel.server.crypto;

import com.qzero.tunnel.server.crypto.modules.PlainModule;
import com.qzero.tunnel.server.crypto.modules.TestModule;

public class CryptoModuleFactory {

    public static CryptoModule getModule(String name){
        switch (name){
            case "plain":
                return new PlainModule();
            case "test":
                return new TestModule();
            default:
                return null;
        }
    }

    public static boolean hasModule(String name){
        switch (name){
            case "plain":
            case "test":
                return true;
            default:
                return false;
        }
    }

}
