package com.qzero.tunnel.server.relay.reception;

import com.qzero.tunnel.server.SpringUtil;
import com.qzero.tunnel.server.tunnel.TunnelService;
import com.qzero.tunnel.server.tunnel.operator.NATTraverseTunnelOperator;
import com.qzero.tunnel.server.tunnel.operator.TunnelOperator;
import com.qzero.tunnel.server.tunnel.operator.VirtualNetworkOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
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

        String configLine;
        try {
            configLine=br.readLine();
        } catch (IOException e) {
            log.trace("Failed to read config line from client "+clientIp+", connection closed",e);
            try {
                clientSocket.close();
            }catch (Exception e1){

            }
            return;
        }

        String[] parts=configLine.split(" ");

        int tunnelPort;
        String sessionId;
        try {
            tunnelPort=Integer.parseInt(parts[0]);
            sessionId=parts[1];
        }catch (Exception e){
            pw.println("Failed to convert config value for config line, please connect again and use <tunnelPort> <sessionId>");
            pw.flush();
            try {
                clientSocket.close();
            }catch (Exception e1){

            }
            return;
        }

        TunnelService tunnelManager= SpringUtil.getBean(TunnelService.class);

        TunnelOperator tunnelOperator=tunnelManager.getTunnelOperator(tunnelPort);

        if(tunnelOperator==null || !tunnelOperator.isTunnelRunning()){
            pw.println("Tunnel with port "+tunnelPort+" is not running");
            pw.flush();
            try {
                clientSocket.close();
            }catch (Exception e1){}
            return;
        }

        if(tunnelOperator instanceof NATTraverseTunnelOperator){
            NATTraverseTunnelOperator operator= (NATTraverseTunnelOperator) tunnelOperator;
            try {
                operator.startRelaySession(sessionId,clientSocket);
            } catch (Exception e) {
                pw.println("Failed to start relay session with client\nReason: "+e.getMessage());
                pw.flush();
                try {
                    clientSocket.close();
                }catch (Exception e1){}
                return;
            }
        }else if(tunnelOperator instanceof VirtualNetworkOperator){
            VirtualNetworkOperator operator=(VirtualNetworkOperator) tunnelOperator;
            try {
                operator.startRelaySession(sessionId,clientSocket);
            }catch (Exception e){
                pw.println("Failed to start relay session with client\nReason: "+e.getMessage());
                pw.flush();
                try {
                    clientSocket.close();
                }catch (Exception e1){}
                return;
            }
        }else{
            pw.println("Tunnel with port "+tunnelPort+" is not a supported type");
            pw.flush();
            try {
                clientSocket.close();
            }catch (Exception e1){}
            return;
        }
    }
}
