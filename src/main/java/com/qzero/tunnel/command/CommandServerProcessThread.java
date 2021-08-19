package com.qzero.tunnel.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class CommandServerProcessThread extends Thread {

    private Logger log= LoggerFactory.getLogger(getClass());

    private Socket clientSocket;
    private PrintWriter pw;
    private BufferedReader br;

    private String clientIp;

    private boolean authorized=false;

    public CommandServerProcessThread(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        clientIp=clientSocket.getInetAddress().getHostAddress();

        pw=new PrintWriter(clientSocket.getOutputStream());
        br=new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    @Override
    public void run() {
        super.run();

        pw.println("Now you have connected to command server successfully");
        pw.println("To continue, please input your authorize code");
        pw.flush();

        try {
            while (true){
                String commandLine=br.readLine();
                processCommandLine(commandLine);
            }
        }catch (Exception e){
            log.error("Failed to continue to interact with client "+clientIp,e);
        }

        try {
            clientSocket.close();
            log.info("Command server lost connection with client "+clientIp);
        }catch (Exception e){
            log.error("Failed to close connection with client "+clientIp,e);
        }
    }

    private void processCommandLine(String commandLine){

    }

}
