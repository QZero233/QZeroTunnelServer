package com.qzero.tunnel.server.command.executor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CommandMethod {

    String commandName();
    int parameterCount() default 0;

}
