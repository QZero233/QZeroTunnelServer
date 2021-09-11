package com.qzero.tunnel.server.command;

import com.qzero.tunnel.server.GlobalCommandServerClientContainer;
import com.qzero.tunnel.server.SpringUtil;
import com.qzero.tunnel.server.tunnel.GlobalTunnelManager;
import com.qzero.tunnel.server.authorize.AuthorizeHelper;
import com.qzero.tunnel.server.data.TunnelUser;
import com.qzero.tunnel.server.data.TunnelConfig;
import com.qzero.tunnel.server.tunnel.TunnelDoesNotExistException;
import com.qzero.tunnel.server.tunnel.TunnelPortOccupiedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class CommandServerClientProcessThread extends Thread {

    private Logger log= LoggerFactory.getLogger(getClass());

    private Socket clientSocket;
    private PrintWriter pw;
    private BufferedReader br;

    private String clientIp;

    private TunnelUser authorizeInfo;

    private AuthorizeHelper authorizeHelper= AuthorizeHelper.getInstance();

    private GlobalTunnelManager globalTunnelManager=GlobalTunnelManager.getInstance();

    public CommandServerClientProcessThread(Socket clientSocket) throws IOException{
        this.clientSocket = clientSocket;
        clientIp=clientSocket.getInetAddress().getHostAddress();

        pw=new PrintWriter(clientSocket.getOutputStream());
        br=new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    @Override
    public void run() {
        super.run();

        writeToClientWithLn("Now you have connected to command server successfully\n" +
                "To continue, please use login <username> <password> to login");

        try {
            while (!isInterrupted()){
                String commandLine=br.readLine();
                if(commandLine==null)
                    break;
                processCommandLine(commandLine);
            }
        }catch (Exception e){
            if(isInterrupted()){
                log.trace(String.format("The process thread for client %s has been stopped", clientIp));
                return;
            }
            log.error("Failed to continue to interact with client "+clientIp,e);
        }

        try {
            clientSocket.close();
            log.trace("Command server lost connection with client "+clientIp);
        }catch (Exception e){
            log.trace("Failed to close connection with client "+clientIp,e);
        }

        if (authorizeInfo != null) {
            //Client offline
            try {
                globalTunnelManager.closeAllTunnelByOwner(authorizeInfo.getUsername());
            } catch (IOException e) {
                log.error("Failed to close all tunnels opened by "+authorizeInfo.getUsername());
            }
            GlobalCommandServerClientContainer.getInstance().removeClient(authorizeInfo.getUsername());
        }
    }

    public void closeConnection() throws IOException {
        writeToClientWithLn("Stopped");
        interrupt();
        clientSocket.close();
    }

    public void writeToClientWithLn(String msg){
        writeToClient(msg+"\n");
    }

    public void writeToClient(String msg){
        synchronized (pw){
            pw.print(msg);
            pw.flush();
        }
    }

    private void processCommandLine(String commandLine){
        if(authorizeInfo==null && !commandLine.startsWith("login")){
            writeToClientWithLn(String.format("Failed to execute command %s, you have not logged in yet", commandLine));
            return;
        }

        String commandParts[]=commandLine.split(" ");
        String commandName=commandParts[0];

        if(commandName.equals("login")){
            if(authorizeInfo!=null){
                writeToClientWithLn("You have already logged in");
                return;
            }

            if(commandParts.length<3){
                writeToClientWithLn("You must add username and password as parameters");
                return;
            }

            try {
                if(!authorizeHelper.checkAuthorize(new TunnelUser(commandParts[1],commandParts[2]))){
                    writeToClientWithLn("Login failed, please check and try again");
                    return;
                }

                authorizeInfo=authorizeHelper.getUser(commandParts[1]);
                writeToClientWithLn("You have logged in successfully, welcome user "+authorizeInfo.getUsername());

                GlobalCommandServerClientContainer container=GlobalCommandServerClientContainer.getInstance();
                if(container.hasOnlineClient(authorizeInfo.getUsername())){
                    container.getClient(authorizeInfo.getUsername()).closeConnection();
                    container.removeClient(authorizeInfo.getUsername());
                }

                container.addClient(authorizeInfo.getUsername(),this);
                log.trace(String.format("User %s has logged in", authorizeInfo.getUsername()));
            }catch (Exception e){
                log.error(String.format("Failed to login(command line : %s)", commandLine),e);
                writeToClientWithLn("Login failed, some error occurred, please contact admin for detailed log");
                return;
            }
        }

        if(commandName.equals("update_tunnel")){
            if(commandParts.length<4){
                writeToClientWithLn("Command new_tunnel need at least 3 parameter");
                return;
            }

            int tunnelPort,localPort;
            try {
                tunnelPort=Integer.parseInt(commandParts[1]);
                localPort=Integer.parseInt(commandParts[3]);
                if(tunnelPort<=0 || localPort<=0)
                    throw new Exception();
            }catch (Exception e){
                writeToClientWithLn(String.format("Illegal port number, please check tunnel port %s and local port %s",
                        commandParts[1],commandParts[3]));
                return;
            }

            String localIp=commandParts[2];

            try {
                TunnelConfig config=new TunnelConfig(tunnelPort,authorizeInfo.getUsername(),localIp,localPort);
                globalTunnelManager.updateTunnel(config);
                writeToClientWithLn("Tunnel updated on port "+tunnelPort);
            }catch (TunnelDoesNotExistException tunnelDoesNotExistException){
                writeToClientWithLn(tunnelDoesNotExistException.getMessage());
            }

            return;
        }

        if(commandName.equals("new_tunnel")){
            if(commandParts.length<4){
                writeToClientWithLn("Command new_tunnel need at least 3 parameter");
                return;
            }

            int tunnelPort,localPort;
            try {
                tunnelPort=Integer.parseInt(commandParts[1]);
                localPort=Integer.parseInt(commandParts[3]);
                if(tunnelPort<=0 || localPort<=0)
                    throw new Exception();
            }catch (Exception e){
                writeToClientWithLn(String.format("Illegal port number, please check tunnel port %s and local port %s",
                        commandParts[1],commandParts[3]));
                return;
            }

            String localIp=commandParts[2];

            try {
                TunnelConfig config=new TunnelConfig(tunnelPort,authorizeInfo.getUsername(),localIp,localPort);
                globalTunnelManager.newTunnel(config);
                writeToClientWithLn("Tunnel created on port "+tunnelPort);
            }catch (TunnelPortOccupiedException tunnelPortOccupiedException){
                writeToClientWithLn(tunnelPortOccupiedException.getMessage());
            }

            return;
        }

        if(commandName.equals("open_tunnel")){
            if(commandParts.length<2){
                writeToClientWithLn("Command open_tunnel need at least 1 parameter");
                return;
            }

            int port;
            try {
                port=Integer.parseInt(commandParts[1]);
                if(port<=0)
                    throw new Exception();
            }catch (Exception e){
                writeToClientWithLn("Illegal port number "+commandParts[1]);
                return;
            }

            try {
                globalTunnelManager.openTunnel(port);
                writeToClientWithLn("Tunnel opened on port "+port);
            }catch (TunnelDoesNotExistException tunnelDoesNotExistException){
                writeToClientWithLn(tunnelDoesNotExistException.getMessage());
            }catch (IOException e){
                writeToClientWithLn(String.format("Failed to open tunnel on port %d, for detailed log please contact admin", port));
                log.trace("Failed to open tunnel on port "+port,e);
            }

            return;

        }

        if(commandName.endsWith("close_tunnel")){
            if(commandParts.length<2){
                writeToClientWithLn("Command close_tunnel need at least 1 parameter");
                return;
            }

            int port;
            try {
                port=Integer.parseInt(commandParts[1]);
                if(port<=0)
                    throw new Exception();
            }catch (Exception e){
                writeToClientWithLn("Illegal port number "+commandParts[1]);
                return;
            }

            if(!globalTunnelManager.hasTunnel(port)){
                writeToClientWithLn(String.format("Tunnel on port %d does not exist", port));
                return;
            }

            try {
                globalTunnelManager.closeTunnel(port);
                writeToClientWithLn(String.format("Tunnel on port %d has been closed", port));
            }catch (TunnelDoesNotExistException tunnelDoesNotExistException){
                writeToClientWithLn(tunnelDoesNotExistException.getMessage());
            } catch (IOException e) {
                writeToClientWithLn(String.format("Failed to close tunnel on port %d, for detailed log please contact admin", port));
                log.trace("Failed to close tunnel on port "+port,e);
            }

            return;
        }


    }

}
