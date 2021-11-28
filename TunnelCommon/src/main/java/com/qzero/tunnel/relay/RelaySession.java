package com.qzero.tunnel.relay;

import com.qzero.tunnel.crypto.CryptoException;
import com.qzero.tunnel.crypto.CryptoModule;
import com.qzero.tunnel.crypto.DataWithLength;
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

            if(closeCallback!=null)
                closeCallback.callback();
        };

        //Read from direct
        //And send to tunnel
        directToTunnel=new RelayThread(directClient, tunnelClient, synchronizedDisconnectListener, new DataPreprocessor() {
            @Override
            public DataWithLength beforeSent(DataWithLength data) {
                if(tunnelToServerModule!=null){
                    try {
                        return tunnelToServerModule.encrypt(data);
                    }catch (CryptoException e){
                        log.error("Failed to encrypted data",e);
                        return null;
                    }
                }else{
                    return data;
                }
            }

            @Override
            public DataWithLength afterReceived(DataWithLength data) {
                if(directToServerModule!=null){
                    try {
                        return directToServerModule.decrypt(data);
                    }catch (CryptoException e){
                        log.error("Failed to decrypted data",e);
                        return null;
                    }
                }else{
                    return data;
                }
            }

        });


        //Read from tunnel
        //And send to direct
        tunnelToDirect=new RelayThread(tunnelClient, directClient, synchronizedDisconnectListener, new DataPreprocessor() {
            @Override
            public DataWithLength beforeSent(DataWithLength data) {
                if(directToServerModule!=null){
                    try {
                        return directToServerModule.encrypt(data);
                    }catch (CryptoException e){
                        log.error("Failed to encrypted data",e);
                        return null;
                    }
                }else{
                    return data;
                }
            }

            @Override
            public DataWithLength afterReceived(DataWithLength data) {
                if(tunnelToServerModule!=null){
                    try {
                        return tunnelToServerModule.decrypt(data);
                    }catch (CryptoException e){
                        log.error("Failed to decrypted data",e);
                        return null;
                    }
                }else{
                    return data;
                }
            }

        });
        tunnelToDirect.setSourceTunnel(true);

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

        if(closeCallback!=null)
            closeCallback.callback();
    }

}
