package com.qzero.tunnel.server.crypto;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CryptoModuleClass {

    String name();

}
