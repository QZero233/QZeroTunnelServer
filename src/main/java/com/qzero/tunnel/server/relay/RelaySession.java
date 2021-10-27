package com.qzero.tunnel.server.relay;

import com.qzero.tunnel.server.crypto.CryptoModule;
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

    private CryptoModule tunnelToServerModule;
    private CryptoModule directToServerModule;

    public void setCloseCallback(RelaySessionCloseCallback closeCallback) {
        this.closeCallback = closeCallback;
    }

    public void setDirectClient(Socket directClient) {
        this.directClient = directClient;
    }

    public void setTunnelClient(Socket tunnelClient) {
        this.tunnelClient = tunnelClient;
    }

    public void initializeCryptoModule(CryptoModule tunnelToServerModule,CryptoModule directToServerModule){
        this.tunnelToServerModule=tunnelToServerModule;
        this.directToServerModule=directToServerModule;
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

        directToTunnel=new RelayThread(directClient, tunnelClient, synchronizedDisconnectListener, new DataPreprocessor() {
            @Override
            public byte[] beforeSent(byte[] data) {
                if(tunnelToServerModule!=null){
                    return tunnelToServerModule.encrypt(data);
                }else{
                    return data;
                }
            }

            @Override
            public byte[] afterReceived(byte[] data) {
                if(directToServerModule!=null){
                    return directToServerModule.decrypt(data);
                }else{
                    return data;
                }
            }
        });


        tunnelToDirect=new RelayThread(tunnelClient, directClient, synchronizedDisconnectListener, new DataPreprocessor() {
            @Override
            public byte[] beforeSent(byte[] data) {
                if(directToServerModule!=null){
                    return directToServerModule.encrypt(data);
                }else{
                    return data;
                }
            }

            @Override
            public byte[] afterReceived(byte[] data) {
                if(tunnelToServerModule!=null){
                    return tunnelToServerModule.decrypt(data);
                }else{
                    return data;
                }
            }
        });

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
