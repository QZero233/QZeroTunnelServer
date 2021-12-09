package com.qzero.tunnel.server.relay.remind;

import com.qzero.tunnel.server.SpringUtil;
import com.qzero.tunnel.server.authorize.AuthorizeService;
import com.qzero.tunnel.server.data.NATTraverseMapping;
import com.qzero.tunnel.server.data.TunnelConfig;
import com.qzero.tunnel.server.data.TunnelUser;
import com.qzero.tunnel.server.exception.ResponsiveException;
import com.qzero.tunnel.server.tunnel.TunnelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class RemindClientProcessThread extends Thread {

    private Logger log= LoggerFactory.getLogger(getClass());

    private Socket clientSocket;

    private String clientIp;

    private PrintWriter pw;
    private BufferedReader br;

    private String username=null;

    public RemindClientProcessThread(Socket clientSocket) throws Exception {
        this.clientSocket = clientSocket;
        clientIp=clientSocket.getInetAddress().getHostAddress();

        pw=new PrintWriter(clientSocket.getOutputStream());
        br=new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    @Override
    public void run() {
        super.run();

        //To check if the client is still alive
        try {
            while (!isInterrupted()){
                String line=br.readLine();
                //Client disconnected
                if(line==null){
                    interrupt();
                    break;
                }
                processInputLine(line);
            }
        }catch (Exception e){
            if(isInterrupted()){
                log.trace(String.format("The process thread for client %s has been stopped", clientIp));
                return;
            }

            if(!(e instanceof SocketException)){
                log.error("Failed to continue to interact with client "+clientIp+" due to unknown reason",e);
            }
        }

        try {
            clientSocket.close();
            log.trace("Relay session remind server lost connection with client "+clientIp);
        }catch (Exception e){
            log.trace("Failed to close connection with client "+clientIp,e);
        }

        if (username != null) {
            //Client offline
            try {
                TunnelService tunnelManager=SpringUtil.getBean(TunnelService.class);
                tunnelManager.closeAllTunnelByOwner(username);
            } catch (Exception e) {
                log.error("Failed to close all tunnels opened by "+username);
            }
            RemindClientContainer.getInstance().removeClient(username);
        }
    }

    private void processInputLine(String line){
        if(username!=null)
            return;
        //Only accept token
        AuthorizeService authorizeService= SpringUtil.getBean(AuthorizeService.class);
        TunnelUser user;
        try {
            user = authorizeService.getUserByToken(line);
        } catch (ResponsiveException e) {
            pw.println("failed");
            pw.flush();
            return;
        }
        username=user.getUsername();
        pw.println("succeeded");
        pw.flush();

        //Check if another client with the same username is online
        RemindClientContainer container=RemindClientContainer.getInstance();
        if(container.hasOnlineClient(username)){
            //Make it offline
            RemindClientProcessThread processThread=container.getClient(username);
            processThread.closeConnection();
            container.removeClient(username);
        }

        //Add into map
        container.addClient(username,this);
    }

    public void closeConnection(){
        interrupt();
        try {
            clientSocket.close();
        } catch (IOException e) {
            log.error("Failed to close NAT Traverse remind connection with client "+clientIp,e);
        }
    }

    public void remindRelayConnect(TunnelConfig config, NATTraverseMapping mapping, String sessionId) {
        //Let client establish connection with local server
        pw.println(String.format("%d %s %s %d %s", config.getTunnelPort(), sessionId,
                mapping.getLocalIp(), mapping.getLocalPort(),config.getCryptoModuleName()));
        pw.flush();
    }

}
