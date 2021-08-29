package com.qzero.tunnel.server.tunnel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GlobalTunnelManager {

    private Map<Integer, TunnelServerThread> tunnelMap=new HashMap<>();

    private static GlobalTunnelManager instance;

    public static GlobalTunnelManager getInstance() {
        if(instance==null)
            instance=new GlobalTunnelManager();
        return instance;
    }

    private GlobalTunnelManager(){

    }

    public void openTunnel(int port,String usernameOfOpener) throws IOException, TunnelPortOccupiedException {
        if(tunnelMap.containsKey(port))
            throw new TunnelPortOccupiedException(port);

        TunnelServerThread tunnelThread=new TunnelServerThread(port,usernameOfOpener);
        tunnelThread.start();
        tunnelMap.put(port,tunnelThread);
    }

    public void closeTunnel(int port) throws IOException {
        TunnelServerThread tunnelThread=tunnelMap.get(port);
        tunnelThread.closeTunnel();
        tunnelMap.remove(port);
    }

    public boolean hasTunnel(int port){
        return tunnelMap.containsKey(port);
    }

}
