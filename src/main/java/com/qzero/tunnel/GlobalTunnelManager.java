package com.qzero.tunnel;

import com.qzero.tunnel.reception.TunnelServerThread;

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

    public void openTunnel(int port) throws IOException {
        TunnelServerThread tunnelThread=new TunnelServerThread(port);
        tunnelThread.start();
        tunnelMap.put(port,tunnelThread);
    }

    public void closeTunnel(int port) throws IOException {
        TunnelServerThread tunnelThread=tunnelMap.get(port);
        tunnelThread.closeTunnel();
    }

}
