package com.qzero.tunnel.server.config.utils;

public interface ConfigFieldConverter {

    Class dstType();

    Object convert(String origin);

}
