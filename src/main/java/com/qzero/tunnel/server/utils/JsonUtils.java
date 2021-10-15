package com.qzero.tunnel.server.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {

    private static ObjectMapper objectMapper=new ObjectMapper();

    public static String toJson(Object obj){
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

}
