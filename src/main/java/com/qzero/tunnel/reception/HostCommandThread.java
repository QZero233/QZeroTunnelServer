package com.qzero.tunnel.reception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class HostCommandThread extends Thread {

    private Logger log= LoggerFactory.getLogger(getClass());

    private Socket socket;

    private TunnelServerThread tunnelThread;

    public HostCommandThread(Socket socket, TunnelServerThread tunnelThread) {
        this.socket = socket;
        this.tunnelThread = tunnelThread;
    }

    @Override
    public void run() {
        super.run();

        try {
            Scanner scanner=new Scanner(socket.getInputStream());
            while (true){
                String commandLine=scanner.nextLine();
                if(commandLine.startsWith("asHost")){
                    tunnelThread.setHost(socket);
                }else if(commandLine.startsWith("contact")){
                    String clientId=commandLine.replace("contact ","");
                    tunnelThread.contactClient(clientId,socket);
                    break;
                }
            }
        } catch (IOException e) {
            log.debug("Failed to read command input",e);
        }

    }
}
