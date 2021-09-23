package com.qzero.tunnel.server.tunnel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TunnelServerThread extends Thread {

    private Logger log= LoggerFactory.getLogger(getClass());

    private int tunnelPort;

    private ServerSocket serverSocket;

    private TunnelOperator.newClientConnectedCallback callback;

    public TunnelServerThread(int tunnelPort,TunnelOperator.newClientConnectedCallback callback) throws IOException {
        this.tunnelPort = tunnelPort;
        this.callback=callback;
    }

    public void initializeServer() throws IOException {
        serverSocket=new ServerSocket(tunnelPort);
    }

    @Override
    public void run() {
        super.run();
        log.trace(String.format("Tunnel has started on port %d successfully", tunnelPort));

        try {
            while (!isInterrupted()) {
                Socket socket = serverSocket.accept();
                callback.onConnected(socket);
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
