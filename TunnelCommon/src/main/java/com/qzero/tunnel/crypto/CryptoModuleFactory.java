package com.qzero.tunnel.crypto;

import com.qzero.tunnel.crypto.modules.TestModule;
import com.qzero.tunnel.crypto.modules.PlainModule;

public class CryptoModuleFactory {

    public static CryptoModule getModule(String name){
        switch (name){
            case "plain":
                return new PlainModule(0);
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
