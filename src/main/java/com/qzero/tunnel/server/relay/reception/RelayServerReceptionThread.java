package com.qzero.tunnel.server.relay.reception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class RelayServerReceptionThread extends Thread {

    private Logger log= LoggerFactory.getLogger(getClass());

    private ServerSocket serverSocket;

    private int port;

    public RelayServerReceptionThread(int port) throws IOException {
        this.port=port;
        serverSocket=new ServerSocket(port);
    }

    @Override
    public void run() {
        super.run();

        log.info(String.format("NAT traverse relay reception server has started on port %d successfully", port));

        try {
            while (!isInterrupted()){
                Socket socket=serverSocket.accept();
                String ip=socket.getInetAddress().getHostAddress();

                //Input sessionId in one line
                try{
                    new RelayServerReceptionProcessThread(socket).start();
                }catch (Exception e){
                    log.trace("Failed to initialize process thread for client with ip "+ip,e);
                }
            }
        }catch (Exception e){
            log.error("Failed to accept relay client, no more client will be accepted from now on, relay reception server has stopped just now",e);
        }

    }

}
