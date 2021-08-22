package com.qzero.tunnel.server.config.utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigValueConvert {
    Class<? extends ConfigFieldConverter> converter();
}
