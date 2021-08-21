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
            log.debug("Relay session stopped, for one client has disconnected");

            directToTunnel.interrupt();
            tunnelToDirect.interrupt();

            try {
                directClient.close();
            }catch (Exception e){
                log.error("Failed to close direct client connection",e);
            }

            try {
                tunnelClient.close();
            }catch (Exception e){
                log.error("Failed to close tunnel client connection",e);
            }
        };

        directToTunnel=new RelayThread(directClient,tunnelClient,synchronizedDisconnectListener);
        tunnelToDirect=new RelayThread(tunnelClient,directClient,synchronizedDisconnectListener);

        directToTunnel.start();
        tunnelToDirect.start();
    }

    public void stopRelay(){
        try {
            directToTunnel.interrupt();
            tunnelToDirect.interrupt();

            directClient.close();
            tunnelClient.close();
        }catch (Exception e){
            log.error("Failed to stop relay session",e);
        }
        log.info("Relay session has stopped");
    }

}
