package com.qzero.tunnel.server.command;

import com.qzero.tunnel.server.GlobalCommandServerClientContainer;
import com.qzero.tunnel.server.command.executor.CommandExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;

public class CommandServerClientProcessThread extends Thread {

    private Logger log= LoggerFactory.getLogger(getClass());

    private Socket clientSocket;
    private PrintWriter pw;
    private BufferedReader br;

    private String clientIp;

    private String clientId;

    private boolean authorized=false;

    private static final String AUTHORIZE_CODE="123456";//FIXME config by files

    private CommandExecutor executor;

    public CommandServerClientProcessThread(Socket clientSocket,String clientId) throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        this.clientSocket = clientSocket;
        this.clientId=clientId;
        clientIp=clientSocket.getInetAddress().getHostAddress();

        pw=new PrintWriter(clientSocket.getOutputStream());
        br=new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        executor=CommandExecutor.getInstance();
    }

    @Override
    public void run() {
        super.run();

        writeToClientWithLn("Now you have connected to command server successfully\n" +
                "To continue, please use login <authorize_code> to login");

        try {
            while (!isInterrupted()){
                String commandLine=br.readLine();
                processCommandLine(commandLine);
            }
        }catch (Exception e){
            if(isInterrupted()){
                log.info(String.format("The process thread for client %s has been stopped", clientIp));
                return;
            }
            log.error("Failed to continue to interact with client "+clientIp,e);
        }

        try {
            clientSocket.close();
            log.info("Command server lost connection with client "+clientIp);
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
        if(!authorized && !commandLine.startsWith("login")){
            writeToClientWithLn(String.format("Failed to execute command %s, you have not logged in yet", commandLine));
            return;
        }

        if(commandLine.startsWith("login")){
            if(authorized){
                writeToClientWithLn("You have already logged in");
                return;
            }

            String[] commandParts=commandLine.split(" ");
            if(commandParts.length<2){
                writeToClientWithLn("You must add authorize code as a parameter");
                return;
            }

            if(commandParts[1].equals(AUTHORIZE_CODE)){
                writeToClientWithLn("You have logged in successfully");
                authorized=true;
                return;
            }else{
                writeToClientWithLn("Wrong authorize code, please check and try again");
                return;
            }
        }

        String returnMsg=executor.executeCommand(commandLine);
        writeToClientWithLn(returnMsg);
    }

}
