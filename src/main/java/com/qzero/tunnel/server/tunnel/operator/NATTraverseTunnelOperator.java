package com.qzero.tunnel.server.tunnel.operator;

import com.qzero.tunnel.crypto.CryptoModule;
import com.qzero.tunnel.crypto.CryptoModuleFactory;
import com.qzero.tunnel.relay.RelaySession;
import com.qzero.tunnel.server.SpringUtil;
import com.qzero.tunnel.server.data.NATTraverseMapping;
import com.qzero.tunnel.server.data.TunnelConfig;
import com.qzero.tunnel.server.data.repositories.NATTraverseMappingRepository;
import com.qzero.tunnel.server.relay.remind.RemindClientContainer;
import com.qzero.tunnel.server.relay.remind.RemindClientProcessThread;
import com.qzero.tunnel.utils.UUIDUtils;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;

public class NATTraverseTunnelOperator extends BaseTunnelOperator implements TunnelOperator {

    private NATTraverseMapping mapping;

    private RemindClientContainer clientContainer= RemindClientContainer.getInstance();

    private Logger log= LoggerFactory.getLogger(getClass());

    public NATTraverseTunnelOperator(TunnelConfig config) {
        super(config);
    }

    public void openTunnel() throws Exception {
        NATTraverseMappingRepository mappingRepository= SpringUtil.getBean(NATTraverseMappingRepository.class);
        mapping=mappingRepository.getById(config.getTunnelPort());
        if(mapping==null){
            throw new Exception("can not find NAT traverse mapping rule");
        }

        mapping= (NATTraverseMapping) Hibernate.unproxy(mapping);

        super.openTunnel();
    }

    @Override
    protected void onNewClientConnected(Socket socket) {
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
