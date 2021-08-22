package com.qzero.tunnel.server.utils;

import java.beans.Transient;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class ConfigurationUtils {

    public static Map<String,String> readConfiguration(File configFile) throws IOException {
        byte[] buf=StreamUtils.readFile(configFile);
        if(buf==null)
            throw new IllegalArgumentException("Configuration file is empty");

        String configString=new String(buf);
        if(configFile.equals(""))
            throw new IllegalArgumentException("Configuration file is empty");

        configString=configString.replaceAll("\r\n","\n");

        Map<String,String> result=new HashMap<>();

        String[] configLines=configString.split("\n");
        for(int i=0;i<configLines.length;i++){
            String configLine=configLines[i];

            //If start with #, regard as remark
            if(configLine.startsWith("#"))
                continue;

            String[] parts=configLine.split("=");
            if(parts.length==1){
                continue;
            }

            String key=parts[0];
            String value=configLine.replaceFirst("^.*?=","");

            result.put(key,value);
        }

        return result;
    }

    public static void writeConfiguration(File configFile,Map<String,String> configMap) throws IOException{
        if(configMap==null || configMap.isEmpty())
            return;

        StringBuffer config=new StringBuffer();
        Set<String>keySet=configMap.keySet();
        for(String key:keySet){
            String value=configMap.get(key);

            config.append(key);
            config.append("=");
            config.append(value);
            config.append("\n");
        }

        byte[] buf=config.toString().getBytes();
        StreamUtils.writeFile(configFile,buf);
    }

    public static void updateConfiguration(File configFile,String key,String value) throws IOException{
        Map<String,String> config=new HashMap<>();
        try {
            config=readConfiguration(configFile);
        }catch (Exception e){

        }

        if(value.equals("")){
            config.remove(key);
        }else{
            config.put(key,value);
        }

        writeConfiguration(configFile,config);
    }

    public static<T> T configToJavaBeanWithOnlyStringFields(Map<String,String> config, Class<T> cls) throws IllegalAccessException, InstantiationException {
        Field[] fields=cls.getDeclaredFields();
        T result=cls.newInstance();
        for(Field field:fields){
            int modifiers=field.getModifiers();
            if(Modifier.isFinal(modifiers) || Modifier.isStatic(modifiers))
                continue;

            if(!field.getType().equals(String.class))
                continue;

            if(field.getAnnotation(Transient.class)!=null)
                continue;

            field.setAccessible(true);

            String key=field.getName();
            if(!config.containsKey(key))
                continue;

            String value=config.get(key);

            field.set(result,value);
        }
        return result;
    }

    public static List<String> readListConfiguration(File configFile) throws IOException {
        byte[] buf=StreamUtils.readFile(configFile);
        if(buf==null)
            throw new IllegalArgumentException("Configuration file is empty");

        String configString=new String(buf);
        String[] lines=configString.split("\n");
        List<String> result=new ArrayList<>();
        for(String line:lines){
            result.add(line);
        }

        return result;
    }

    public static void writeListConfiguration(List<String> config,File configFile) throws IOException {
        StringBuffer stringBuffer=new StringBuffer();
        for(String line:config){
            stringBuffer.append(line);
            stringBuffer.append("\n");
        }

        StreamUtils.writeFile(configFile,stringBuffer.toString().getBytes());
    }

}
