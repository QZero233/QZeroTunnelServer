package com.qzero.tunnel.server.tunnel;

import com.qzero.tunnel.server.crypto.CryptoModule;
import com.qzero.tunnel.server.crypto.CryptoModuleFactory;
import com.qzero.tunnel.server.crypto.modules.PlainModule;
import com.qzero.tunnel.server.data.TunnelConfig;
import com.qzero.tunnel.server.relay.RelaySession;
import com.qzero.tunnel.server.remind.RemindClientContainer;
import com.qzero.tunnel.server.remind.RemindClientProcessThread;
import com.qzero.tunnel.server.utils.UUIDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TunnelOperator {
    private boolean running=false;

    private TunnelConfig config;

    private TunnelServerThread tunnelServerThread;

    private Map<String,RelaySession> sessionMap=new HashMap<>();

    private RemindClientContainer clientContainer= RemindClientContainer.getInstance();

    private Logger log= LoggerFactory.getLogger(getClass());

    private CryptoModule tunnelToServerModule;

    public interface newClientConnectedCallback{
        void onConnected(Socket socket);
    }

    private newClientConnectedCallback callback=socket -> {
        String ip = socket.getInetAddress().getHostAddress();

        if(!clientContainer.hasOnlineClient(config.getTunnelOwner())){
            try {
                socket.close();
                log.trace(String.format("Close connection with client %s, for opener is not online", ip));
            }catch (Exception e){

            }
            return;
        }


        String sessionId= UUIDUtils.getRandomUUID();
        RelaySession session=new RelaySession();
        session.setDirectClient(socket);

        session.setCloseCallback(() -> {
            sessionMap.remove(sessionId);
        });

        sessionMap.put(sessionId,session);

        RemindClientProcessThread processThread=clientContainer.getClient(config.getTunnelOwner());
        processThread.remindRelayConnect(config,sessionId);
    };

    public TunnelOperator(TunnelConfig config) {
        this.config=config;
    }

    public void openTunnel() throws IOException {
        tunnelToServerModule= CryptoModuleFactory.getModule(config.getCryptoModuleName());
        if(tunnelToServerModule==null){
            throw new IOException(String.format("Crypto module named %s does not exist", config.getCryptoModuleName()));
        }

        tunnelServerThread=new TunnelServerThread(config.getTunnelPort(), callback);
        tunnelServerThread.initializeServer();
        tunnelServerThread.start();
        running=true;
    }

    public void closeTunnel() throws IOException {
        tunnelServerThread.closeTunnel();

        Set<String> keySet=sessionMap.keySet();
        for(String key:keySet){
            sessionMap.get(key).closeSession();
        }
        running=false;
    }

    public boolean isTunnelRunning(){
        return running;
    }

    public void startRelaySession(String sessionId, Socket tunnelSocket) {
        RelaySession session=sessionMap.get(sessionId);

        if(session==null)
            throw new IllegalArgumentException(String.format("Relay session with id %s does not exist", sessionId));

        session.setTunnelClient(tunnelSocket);

        try {
            tunnelToServerModule.doHandshakeAsServer(tunnelSocket.getInputStream(),tunnelSocket.getOutputStream());
        }catch (Exception e){
            log.error(String.format("Failed to handshake with client %s, can not start relay session",
                    tunnelSocket.getInetAddress().getHostAddress()),e);
            session.closeSession();
        }

        session.initializeCryptoModule(tunnelToServerModule,new PlainModule());
        session.startRelay();
    }

    public void updateTunnelConfig(TunnelConfig config){
        this.config=config;
    }
}
