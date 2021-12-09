package com.qzero.tunnel.server.tunnel.operator;

import com.qzero.tunnel.crypto.CryptoModuleFactory;
import com.qzero.tunnel.relay.RelaySession;
import com.qzero.tunnel.server.data.TunnelConfig;
import com.qzero.tunnel.server.tunnel.NewClientConnectedCallback;
import com.qzero.tunnel.server.tunnel.TunnelServerThread;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class BaseTunnelOperator implements TunnelOperator {

    protected TunnelConfig config;

    private NewClientConnectedCallback callback;

    protected Map<String, RelaySession> sessionMap =new HashMap<>();

    private TunnelServerThread tunnelServerThread;

    protected boolean running=false;

    public BaseTunnelOperator(TunnelConfig config) {
        this.config = config;
        callback=clientSocket -> {
            onNewClientConnected(clientSocket);
        };
    }

    @Override
    public void openTunnel() throws Exception {
        if(running){
            throw new Exception("Tunnel is running, can not open it");
        }

        if(!CryptoModuleFactory.hasModule(config.getCryptoModuleName())){
            throw new Exception(String.format("Crypto module named %s does not exist", config.getCryptoModuleName()));
        }

        tunnelServerThread=new TunnelServerThread(config, callback);
        tunnelServerThread.startServerSocket();
        tunnelServerThread.start();
        running=true;
    }

    @Override
    public void closeTunnel() throws Exception {
        if(!running){
            throw new Exception("Tunnel is not running, can not close it");
        }

        tunnelServerThread.closeTunnel();

        Set<String> keySet= sessionMap.keySet();
        for(String key:keySet){
            sessionMap.get(key).closeSession();
        }

        running=false;
    }

    @Override
    public boolean isTunnelRunning() {
        return running;
    }

    /**
     * Called when new client connected to the tunnel server
     * @param clientSocket
     */
    protected abstract void onNewClientConnected(Socket clientSocket);
}
