package com.qzero.tunnel.server.tunnel;

import com.qzero.tunnel.server.GlobalCommandServerClientContainer;
import com.qzero.tunnel.server.command.CommandServerClientProcessThread;
import com.qzero.tunnel.server.relay.RelaySession;
import com.qzero.tunnel.server.utils.UUIDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TunnelServerThread extends Thread {

    private Logger log= LoggerFactory.getLogger(getClass());

    private int tunnelPort;

    private String usernameOfOpener;

    private ServerSocket serverSocket;



    private TunnelOperator.newClientConnectedCallback callback;

    public TunnelServerThread(int tunnelPort,String usernameOfOpener,TunnelOperator.newClientConnectedCallback callback) throws IOException {
        this.tunnelPort = tunnelPort;
        this.usernameOfOpener =usernameOfOpener;
        this.callback=callback;
        serverSocket=new ServerSocket(tunnelPort);
    }

    @Override
    public void run() {
        super.run();

        log.trace(String.format("Tunnel has started on port %d successfully", tunnelPort));

        try {
            while (!isInterrupted()) {
                Socket socket = serverSocket.accept();
                /*String ip = socket.getInetAddress().getHostAddress();

                if(!clientContainer.hasOnlineClient(usernameOfOpener)){
                    try {
                        socket.close();
                        log.trace(String.format("Close connection with client %s, for opener is not online", ip));
                    }catch (Exception e){
                    }
                    continue;
                }*/
                callback.onConnected(socket);
                /*CommandServerClientProcessThread processThread=clientContainer.getClient(usernameOfOpener);
                processThread.writeToClientWithLn("connect_relay_session "+sessionId);*/
            }
        } catch (Exception e) {
            if(isInterrupted()){
                log.trace(String.format("Tunnel on port %d has been closed", tunnelPort));
                return;
            }
            log.trace(String.format("Failed to accept tunnel client on port %d, no more client will be accepted from now on",
                    tunnelPort), e);
        }
    }

    public void closeTunnel() throws IOException {
        interrupt();
        serverSocket.close();
    }
}
