package com.qzero.tunnel.server.tunnel;

import com.qzero.tunnel.server.GlobalCommandServerClientContainer;
import com.qzero.tunnel.server.command.CommandServerClientProcessThread;
import com.qzero.tunnel.server.relay.RelaySession;
import com.qzero.tunnel.server.utils.UUIDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class TunnelOperator {

    private int port;
    private String usernameOfOpener;

    private TunnelServerThread tunnelServerThread;

    private Map<String,RelaySession> sessionMap=new HashMap<>();

    private GlobalCommandServerClientContainer clientContainer=GlobalCommandServerClientContainer.getInstance();

    private Logger log= LoggerFactory.getLogger(getClass());

    public interface newClientConnectedCallback{
        void onConnected(Socket socket);
    }

    private newClientConnectedCallback callback=socket -> {
        String ip = socket.getInetAddress().getHostAddress();

        if(!clientContainer.hasOnlineClient(usernameOfOpener)){
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

        CommandServerClientProcessThread processThread=clientContainer.getClient(usernameOfOpener);
        processThread.writeToClientWithLn(String.format("connect_relay_session %d %s", port,sessionId));
    };

    public TunnelOperator(int port, String usernameOfOpener) {
        this.port = port;
        this.usernameOfOpener = usernameOfOpener;
    }

    public void openTunnel() throws IOException {
        tunnelServerThread=new TunnelServerThread(port, usernameOfOpener, callback);
        tunnelServerThread.start();
    }

    public void closeTunnel() throws IOException {
        tunnelServerThread.closeTunnel();

        Set<String> keySet=sessionMap.keySet();
        for(String key:keySet){
            sessionMap.get(key).closeSession();
        }
    }

    public void startRelaySession(String sessionId,Socket tunnelSocket) throws Exception{
        RelaySession session=sessionMap.get(sessionId);
        if(session==null)
            throw new IllegalArgumentException(String.format("Relay session with id %s does not exist", sessionId));

        session.setTunnelClient(tunnelSocket);
        session.startRelay();
    }

}
