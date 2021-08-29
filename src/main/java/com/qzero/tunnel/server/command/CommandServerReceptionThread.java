package com.qzero.tunnel.server.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class CommandServerReceptionThread extends Thread{

    private Logger log= LoggerFactory.getLogger(getClass());

    private ServerSocket serverSocket;

    private int port;

    public CommandServerReceptionThread(int port) throws IOException {
        this.port=port;
        serverSocket=new ServerSocket(port);
    }

    @Override
    public void run() {
        super.run();

        log.info(String.format("Command server has started on port %d successfully",port));

        try {
            while (!isInterrupted()){
                Socket socket=serverSocket.accept();
                String ip=socket.getInetAddress().getHostAddress();
                try {
                    new CommandServerClientProcessThread(socket).start();
                    log.trace(String.format("Client with ip %s has connected successfully", ip));
                }catch (Exception e){
                    log.trace("Failed to initialize operator for client with ip "+ip,e);
                }
            }
        }catch (Exception e){
            log.error("Failed to accept client, no more client will be accepted from now on, command server has stopped just now",e);
        }

    }
}
