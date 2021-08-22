package com.qzero.tunnel.server.command;

import com.qzero.tunnel.server.GlobalCommandServerClientContainer;
import com.qzero.tunnel.server.authorize.AuthorizeHelper;
import com.qzero.tunnel.server.authorize.TunnelUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private String clientId;

    private TunnelUser authorizeInfo;

    public CommandServerClientProcessThread(Socket clientSocket,String clientId) throws IOException{
        this.clientSocket = clientSocket;
        this.clientId=clientId;
        clientIp=clientSocket.getInetAddress().getHostAddress();

        pw=new PrintWriter(clientSocket.getOutputStream());
        br=new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    @Override
    public void run() {
        super.run();

        writeToClientWithLn("Now you have connected to command server successfully\n" +
                "To continue, please use login <authorize_code> to login");

        try {
            while (!isInterrupted()){
                String commandLine=br.readLine();
                if(commandLine==null)
                    break;
                processCommandLine(commandLine);
            }
        }catch (Exception e){
            if(isInterrupted()){
                log.debug(String.format("The process thread for client %s has been stopped", clientIp));
                return;
            }
            log.error("Failed to continue to interact with client "+clientIp,e);
        }

        try {
            clientSocket.close();
            log.debug("Command server lost connection with client "+clientIp);
        }catch (Exception e){
            log.error("Failed to close connection with client "+clientIp,e);
        }

        GlobalCommandServerClientContainer.getInstance().removeClient(clientId);
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
                if(!AuthorizeHelper.checkAuthorize(new TunnelUser(commandParts[1],commandParts[2]))){
                    writeToClientWithLn("Login failed, please check and try again");
                    return;
                }

                authorizeInfo=AuthorizeHelper.getUser(commandParts[1]);
                writeToClientWithLn("You have logged in successfully, welcome user "+authorizeInfo.getUsername());
            }catch (Exception e){
                log.error(String.format("Failed to login(command line : %s)", commandLine),e);
                writeToClientWithLn("Login failed, some error occurred, please contact admin for detailed log");
                return;
            }
        }

        //TODO process commands
    }

}
