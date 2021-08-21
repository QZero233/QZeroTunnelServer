package com.qzero.tunnel.server.tunnel;

import com.qzero.tunnel.server.GlobalCommandServerClientContainer;
import com.qzero.tunnel.server.GlobalTunnelManager;
import com.qzero.tunnel.server.command.CommandServerClientOperator;
import com.qzero.tunnel.server.relay.RelaySession;
import com.qzero.tunnel.server.relay.RelaySessionContainer;
import com.qzero.tunnel.server.utils.UUIDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TunnelServerThread extends Thread {

    private Logger log= LoggerFactory.getLogger(getClass());

    private int tunnelPort;

    private String clientIdOfOpener;

    private ServerSocket serverSocket;

    private GlobalCommandServerClientContainer clientContainer=GlobalCommandServerClientContainer.getInstance();

    public TunnelServerThread(int tunnelPort,String clientIdOfOpener) throws IOException {
        this.tunnelPort = tunnelPort;
        this.clientIdOfOpener=clientIdOfOpener;
        serverSocket=new ServerSocket(tunnelPort);
    }

    @Override
    public void run() {
        super.run();

        log.info(String.format("Tunnel has started on port %d successfully", tunnelPort));

        RelaySessionContainer relaySessionContainer=RelaySessionContainer.getInstance();

        try {
            while (!isInterrupted()) {
                Socket socket = serverSocket.accept();
                String ip = socket.getInetAddress().getHostAddress();

                if(!clientContainer.isClientOnline(clientIdOfOpener)){
                    try {
                        socket.close();
                        log.info(String.format("Close connection with client %s, for opener is not online", ip));
                        log.info(String.format("Close tunnel on port %d, for opener is not online", tunnelPort));
                        GlobalTunnelManager.getInstance().closeTunnel(tunnelPort);
                    }catch (Exception e){
                    }
                    continue;
                }

                String sessionId= UUIDUtils.getRandomUUID();
                RelaySession session=new RelaySession();
                session.setDirectClient(socket);
                relaySessionContainer.addSession(sessionId,session);

                CommandServerClientOperator clientOperator=clientContainer.getClient(clientIdOfOpener);
                clientOperator.sendCommandToClient("connect_relay_session "+sessionId);
            }
        } catch (Exception e) {
            if(isInterrupted()){
                log.info(String.format("Tunnel on port %d has been closed", tunnelPort));
                return;
            }
            log.error(String.format("Failed to accept tunnel client on port %d, no more client will be accepted from now on",
                    tunnelPort), e);
        }
    }

    public void closeTunnel() throws IOException {
        interrupt();
        serverSocket.close();
    }
}
