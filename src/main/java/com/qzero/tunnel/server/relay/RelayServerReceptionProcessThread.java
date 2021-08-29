package com.qzero.tunnel.server.relay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class RelayServerReceptionProcessThread extends Thread {

    private Logger log= LoggerFactory.getLogger(getClass());

    private Socket clientSocket;
    private PrintWriter pw;
    private BufferedReader br;

    private String clientIp;

    public RelayServerReceptionProcessThread(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;

        clientIp=clientSocket.getInetAddress().getHostAddress();

        pw=new PrintWriter(clientSocket.getOutputStream());
        br=new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    @Override
    public void run() {
        super.run();

        String sessionId;
        try {
            sessionId=br.readLine();
        } catch (IOException e) {
            log.trace("Failed to read config line from client "+clientIp+", connection closed",e);
            try {
                clientSocket.close();
            }catch (Exception e1){

            }
            return;
        }

        RelaySessionContainer sessionContainer=RelaySessionContainer.getInstance();
        RelaySession session=sessionContainer.getSession(sessionId);
        if(session==null){
            pw.println(String.format("Session with id %s does not exist", sessionId));
            pw.flush();
            try {
                clientSocket.close();
            }catch (Exception e){

            }
        }

        //pw.println("Well done");
        //pw.flush();

        session.setTunnelClient(clientSocket);
        session.startRelay();
    }
}
