package com.qzero.tunnel.server.tunnel;

import com.qzero.tunnel.server.GlobalCommandServerClientContainer;
import com.qzero.tunnel.server.command.CommandServerClientProcessThread;
import com.qzero.tunnel.server.data.TunnelConfig;
import com.qzero.tunnel.server.relay.RelaySession;
import com.qzero.tunnel.server.utils.UUIDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class TunnelOperator {
    private boolean running=false;

    private TunnelConfig config;

    private TunnelServerThread tunnelServerThread;

    private Map<String,RelaySession> sessionMap=new HashMap<>();

    private GlobalCommandServerClientContainer clientContainer=GlobalCommandServerClientContainer.getInstance();

    private Logger log= LoggerFactory.getLogger(getClass());

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

        sessionMap.put(sessionId,session);

        CommandServerClientProcessThread processThread=clientContainer.getClient(config.getTunnelOwner());
        processThread.writeToClientWithLn(String.format("connect_relay_session %d %s %s %d", config.getTunnelPort(),sessionId,
                config.getLocalIp(),config.getLocalPort()));
    };

    public TunnelOperator(TunnelConfig config) {
        this.config=config;
    }

    public void openTunnel() throws IOException {
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

    public void startRelaySession(String sessionId,Socket tunnelSocket) throws Exception{
        RelaySession session=sessionMap.get(sessionId);
        if(session==null)
            throw new IllegalArgumentException(String.format("Relay session with id %s does not exist", sessionId));

        session.setTunnelClient(tunnelSocket);
        session.startRelay();
    }

    public void updateTunnelConfig(TunnelConfig config){
        this.config=config;
    }


}
