package com.qzero.tunnel.server;

import com.qzero.tunnel.server.command.CommandServerClientOperator;

import java.util.HashMap;
import java.util.Map;

public class GlobalCommandServerClientContainer {

    private static GlobalCommandServerClientContainer instance;

    private Map<String, CommandServerClientOperator> operatorMap=new HashMap<>();

    private GlobalCommandServerClientContainer(){

    }

    public static GlobalCommandServerClientContainer getInstance() {
        if(instance==null)
            instance=new GlobalCommandServerClientContainer();
        return instance;
    }

    public void addClient(String clientId,CommandServerClientOperator clientOperator){
        operatorMap.put(clientId,clientOperator);
    }

    public CommandServerClientOperator getClient(String clientId){
        return operatorMap.get(clientId);
    }

    public void removeClient(String clientId){
        operatorMap.remove(clientId);
    }

    public boolean isClientOnline(String clientId){
        return operatorMap.containsKey(clientId);
    }

}
