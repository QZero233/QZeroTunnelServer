package com.qzero.tunnel.server;

import com.qzero.tunnel.server.command.CommandServerClientProcessThread;

import java.util.HashMap;
import java.util.Map;

public class GlobalCommandServerClientContainer {

    private static GlobalCommandServerClientContainer instance;

    //Key: UserName
    private Map<String, CommandServerClientProcessThread> processThreadMap=new HashMap<>();

    private GlobalCommandServerClientContainer(){

    }

    public static GlobalCommandServerClientContainer getInstance() {
        if(instance==null)
            instance=new GlobalCommandServerClientContainer();
        return instance;
    }

    public void addClient(String clientUserName,CommandServerClientProcessThread processThread){
        processThreadMap.put(clientUserName,processThread);
    }

    public CommandServerClientProcessThread getClient(String clientUserName){
        return processThreadMap.get(clientUserName);
    }

    public void removeClient(String clientUserName){
        processThreadMap.remove(clientUserName);
    }

    public boolean hasOnlineClient(String clientUserName){
        return processThreadMap.containsKey(clientUserName);
    }

}
