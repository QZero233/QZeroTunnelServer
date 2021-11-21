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

                //Do connect event in another thread
                //Or it will block other client's connection
                //Recorded on 11/20/2021:
                //我艹，找了半天问题终于找到了，如果不在另外一个线程运行，连接远端服务器的操作就会Block掉接收新客户端
                //连接的过程，如果远端服务器一直连不上就会一直卡着
                new Thread(){
                    @Override
                    public void run() {
                        super.run();

                        clientConnectedCallback.onConnected(socket);
                    }
                }.start();

            }
        } catch (Exception e) {
            if(isInterrupted()){
                log.trace(String.format("Tunnel on port %d has been closed", tunnelConfig.getTunnelPort()));
                return;
            }
            log.error(String.format("Failed to accept tunnel client on port %d, no more client will be accepted from now on",
                    tunnelConfig.getTunnelPort()), e);
        }
    }

    public void closeTunnel() throws IOException {
        interrupt();
        serverSocket.close();
    }
}
