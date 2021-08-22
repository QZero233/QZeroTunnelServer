package com.qzero.tunnel.server.config.utils;

public class IntConverter implements ConfigFieldConverter{

    @Override
    public Class dstType() {
        return int.class;
    }

    @Override
    public Object convert(String origin) {
        return Integer.parseInt(origin);
    }
}
