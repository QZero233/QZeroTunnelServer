package com.qzero.tunnel.server;

import com.qzero.tunnel.server.tunnel.TunnelServerThread;

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

    public void openTunnel(int port,String clientIdOfOpener) throws IOException {
        if(tunnelMap.containsKey(port))
            throw new IllegalArgumentException(String.format("Tunnel port %d has already been occupied", port));

        TunnelServerThread tunnelThread=new TunnelServerThread(port,clientIdOfOpener);
        tunnelThread.start();
        tunnelMap.put(port,tunnelThread);
    }

    public void closeTunnel(int port) throws IOException {
        TunnelServerThread tunnelThread=tunnelMap.get(port);
        tunnelThread.closeTunnel();
        tunnelMap.remove(port);
    }

}
