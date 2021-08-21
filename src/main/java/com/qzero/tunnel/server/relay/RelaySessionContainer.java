package com.qzero.tunnel.server.relay;

import java.util.HashMap;
import java.util.Map;

public class RelaySessionContainer {

    private static RelaySessionContainer instance;

    private Map<String,RelaySession> sessionMap=new HashMap<>();

    public static RelaySessionContainer getInstance(){
        if(instance==null)
            instance=new RelaySessionContainer();
        return instance;
    }

    private RelaySessionContainer(){

    }

    public void addSession(String sessionId, RelaySession session){
        sessionMap.put(sessionId,session);
    }

    public void removeSession(String sessionId){
        sessionMap.remove(sessionId);
    }

    public RelaySession getSession(String sessionId){
        return sessionMap.get(sessionId);
    }

}
