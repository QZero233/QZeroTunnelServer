package com.qzero.tunnel.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class CommandServerReceptionThread extends Thread{

    private Logger log= LoggerFactory.getLogger(getClass());

    private ServerSocket serverSocket;

    public CommandServerReceptionThread(int port) throws IOException {
        serverSocket=new ServerSocket(port);
    }

    @Override
    public void run() {
        super.run();

        try {
            while (true){
                Socket socket=serverSocket.accept();

            }
        }catch (Exception e){
            log.error("Failed to accept client, no more client will be accepted from now on",e);
        }

    }
}
