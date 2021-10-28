package com.qzero.tunnel.server.tunnel;

import com.qzero.tunnel.server.data.TunnelConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TunnelServerThread extends Thread {

    private Logger log= LoggerFactory.getLogger(getClass());

    private TunnelConfig tunnelConfig;

    private ServerSocket serverSocket;

    private NewClientConnectedCallback clientConnectedCallback;

    public TunnelServerThread(TunnelConfig tunnelConfig,NewClientConnectedCallback clientConnectedCallback) throws Exception {
        this.tunnelConfig=tunnelConfig;
        this.clientConnectedCallback=clientConnectedCallback;

        if(tunnelConfig==null)
            throw new Exception("Tunnel config can not be null");
    }

    public void startServerSocket() throws IOException {
        serverSocket=new ServerSocket(tunnelConfig.getTunnelPort());
    }

    @Override
    public void run() {
        super.run();
        log.trace(String.format("Tunnel has started on port %d successfully", tunnelConfig.getTunnelPort()));

        try {
            while (!isInterrupted()) {
                Socket socket = serverSocket.accept();
                clientConnectedCallback.onConnected(socket);
            }
        } catch (Exception e) {
            if(isInterrupted()){
                log.trace(String.format("Tunnel on port %d has been closed", tunnelConfig.getTunnelPort()));
                return;
            }
            log.trace(String.format("Failed to accept tunnel client on port %d, no more client will be accepted from now on",
                    tunnelConfig.getTunnelPort()), e);
        }
    }

    public void closeTunnel() throws IOException {
        interrupt();
        serverSocket.close();
    }
}
