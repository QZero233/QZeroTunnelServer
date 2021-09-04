package com.qzero.tunnel.server.tunnel;

import com.qzero.tunnel.server.config.GlobalConfigurationManager;
import com.qzero.tunnel.server.config.ServerConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlobalTunnelManager {

    private Map<Integer, TunnelOperator> tunnelMap=new HashMap<>();

    private static GlobalTunnelManager instance;

    List<String> bannedPorts;

    public static GlobalTunnelManager getInstance() {
        if(instance==null)
            instance=new GlobalTunnelManager();
        return instance;
    }

    private GlobalTunnelManager(){
        ServerConfiguration configuration=GlobalConfigurationManager.getInstance().getServerConfiguration();
        bannedPorts=configuration.getBannedTunnelPorts();
        if(bannedPorts==null)
            bannedPorts=new ArrayList<>();
        bannedPorts.add(configuration.getReceptionServerPort()+"");
        bannedPorts.add(configuration.getCommandServerPort()+"");
    }

    public void openTunnel(int port,String usernameOfOpener) throws IOException, TunnelPortOccupiedException {
        if(tunnelMap.containsKey(port) || bannedPorts.contains(port+""))
            throw new TunnelPortOccupiedException(port);

        TunnelOperator operator=new TunnelOperator(port,usernameOfOpener);
        operator.openTunnel();
        tunnelMap.put(port,operator);
    }

    public void closeTunnel(int port) throws IOException {
        TunnelOperator tunnelOperator=tunnelMap.get(port);
        tunnelOperator.closeTunnel();
        tunnelMap.remove(port);
    }

    public TunnelOperator getTunnelOperator(int port){
        return tunnelMap.get(port);
    }

    public boolean hasTunnel(int port){
        return tunnelMap.containsKey(port);
    }

}
