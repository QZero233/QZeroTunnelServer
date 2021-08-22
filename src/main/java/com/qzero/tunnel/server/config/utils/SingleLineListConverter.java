package com.qzero.tunnel.server.config.utils;

import java.util.ArrayList;
import java.util.List;

public class SingleLineListConverter implements ConfigFieldConverter {

    @Override
    public Class dstType() {
        return List.class;
    }

    @Override
    public Object convert(String origin) {
        String[] parts=origin.split(",");
        List<String> result=new ArrayList<>();

        for(String part:parts){
            result.add(part);
        }

        return result;
    }
}
