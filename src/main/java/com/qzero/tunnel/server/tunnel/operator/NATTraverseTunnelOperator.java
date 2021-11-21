package com.qzero.tunnel.server.tunnel.operator;

import com.qzero.tunnel.crypto.CryptoModule;
import com.qzero.tunnel.crypto.CryptoModuleFactory;
import com.qzero.tunnel.crypto.modules.PlainModule;
import com.qzero.tunnel.server.SpringUtil;
import com.qzero.tunnel.server.data.NATTraverseMapping;
import com.qzero.tunnel.server.data.TunnelConfig;
import com.qzero.tunnel.server.data.repositories.NATTraverseMappingRepository;
import com.qzero.tunnel.relay.RelaySession;
import com.qzero.tunnel.server.traverse.remind.RemindClientContainer;
import com.qzero.tunnel.server.traverse.remind.RemindClientProcessThread;
import com.qzero.tunnel.server.tunnel.NewClientConnectedCallback;
import com.qzero.tunnel.server.tunnel.TunnelServerThread;
import com.qzero.tunnel.utils.UUIDUtils;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NATTraverseTunnelOperator implements TunnelOperator {
    private boolean running=false;

    private TunnelConfig config;

    private NATTraverseMapping mapping;

    private TunnelServerThread tunnelServerThread;

    private Map<String,RelaySession> sessionMap=new HashMap<>();

    private RemindClientContainer clientContainer= RemindClientContainer.getInstance();

    private Logger log= LoggerFactory.getLogger(getClass());

    private NewClientConnectedCallback callback= socket -> {
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
        processThread.remindRelayConnect(config,mapping,sessionId);
    };

    public NATTraverseTunnelOperator(TunnelConfig config) {
        this.config=config;
    }

    public void openTunnel() throws Exception {
        if(!CryptoModuleFactory.hasModule(config.getCryptoModuleName())){
            throw new Exception(String.format("Crypto module named %s does not exist", config.getCryptoModuleName()));
        }

        NATTraverseMappingRepository mappingRepository= SpringUtil.getBean(NATTraverseMappingRepository.class);
        mapping=mappingRepository.getById(config.getTunnelPort());
        if(mapping==null){
            throw new Exception("can not find NAT traverse mapping rule");
        }

        mapping= (NATTraverseMapping) Hibernate.unproxy(mapping);

        tunnelServerThread=new TunnelServerThread(config, callback);
        tunnelServerThread.startServerSocket();
        tunnelServerThread.start();
        running=true;
    }

    public void closeTunnel() throws Exception {
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

        CryptoModule tunnelToServerModule=CryptoModuleFactory.getModule(config.getCryptoModuleName());
        try {
            tunnelToServerModule.doHandshakeAsServer(tunnelSocket.getInputStream(),tunnelSocket.getOutputStream());
        }catch (Exception e){
            log.error(String.format("Failed to handshake with client %s, can not start relay session",
                    tunnelSocket.getInetAddress().getHostAddress()),e);
            session.closeSession();
        }

        session.initializeCryptoModule(tunnelToServerModule,null);
        session.startRelay();
    }
}
