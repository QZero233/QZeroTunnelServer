package com.qzero.tunnel.server.remind;

import java.util.HashMap;
import java.util.Map;

public class RemindClientContainer {

    private static RemindClientContainer instance;

    //Key: UserName
    private Map<String, RemindClientProcessThread> processThreadMap=new HashMap<>();

    private RemindClientContainer(){

    }

    public static RemindClientContainer getInstance() {
        if(instance==null)
            instance=new RemindClientContainer();
        return instance;
    }

    public void addClient(String clientUserName, RemindClientProcessThread processThread){
        processThreadMap.put(clientUserName,processThread);
    }

    public RemindClientProcessThread getClient(String clientUserName){
        return processThreadMap.get(clientUserName);
    }

    public void removeClient(String clientUserName){
        processThreadMap.remove(clientUserName);
    }

    public boolean hasOnlineClient(String clientUserName){
        return processThreadMap.containsKey(clientUserName);
    }

}
