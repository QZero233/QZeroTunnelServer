package com.qzero.tunnel.server.relay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;

public class RelaySession {

    private Logger log= LoggerFactory.getLogger(getClass());

    private Socket directClient;
    private Socket tunnelClient;

    private RelayThread directToTunnel;
    private RelayThread tunnelToDirect;

    private RelaySessionCloseCallback closeCallback;

    public void setCloseCallback(RelaySessionCloseCallback closeCallback) {
        this.closeCallback = closeCallback;
    }

    public void setDirectClient(Socket directClient) {
        this.directClient = directClient;
    }

    public void setTunnelClient(Socket tunnelClient) {
        this.tunnelClient = tunnelClient;
    }

    public void startRelay(){
        if(directClient==null || tunnelClient==null)
            throw new IllegalStateException("Clients are not ready, failed to start relay route");

        ClientDisconnectedListener synchronizedDisconnectListener= () -> {
            log.trace("Relay session stopped, for one client has disconnected");

            directToTunnel.interrupt();
            tunnelToDirect.interrupt();

            try {
                directClient.close();
            }catch (Exception e){
                log.trace("Failed to close direct client connection",e);
            }

            try {
                tunnelClient.close();
            }catch (Exception e){
                log.trace("Failed to close tunnel client connection",e);
            }

            closeCallback.callback();
        };

        directToTunnel=new RelayThread(directClient,tunnelClient,synchronizedDisconnectListener);
        tunnelToDirect=new RelayThread(tunnelClient,directClient,synchronizedDisconnectListener);

        directToTunnel.start();
        tunnelToDirect.start();

        log.trace(String.format("Relay route from %s to %s has started", directClient.getInetAddress().getHostAddress(),
                tunnelClient.getInetAddress().getHostAddress()));
    }

    public void closeSession(){
        try {
            if(directToTunnel!=null)
                directToTunnel.interrupt();
            if(tunnelToDirect!=null)
                tunnelToDirect.interrupt();

            if(directClient!=null)
                directClient.close();
            if(tunnelClient!=null)
                tunnelClient.close();
        }catch (Exception e){
            log.trace("Failed to stop relay session",e);
        }
        log.trace("Relay session has stopped");

        closeCallback.callback();
    }

}
