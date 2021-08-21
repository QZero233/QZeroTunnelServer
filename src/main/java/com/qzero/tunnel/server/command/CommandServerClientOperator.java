package com.qzero.tunnel.server.command;

import com.qzero.tunnel.server.GlobalCommandServerClientContainer;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;

public class CommandServerClientOperator {

    private String clientId;
    private Socket socket;

    private CommandServerClientProcessThread processThread;

    public CommandServerClientOperator(String clientId, Socket socket) throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        this.clientId = clientId;
        this.socket = socket;

        processThread=new CommandServerClientProcessThread(socket,clientId);
        processThread.start();
    }

    public void closeConnection() throws IOException {
        processThread.interrupt();
        socket.getInputStream().close();
        socket.getOutputStream().close();
        socket.close();

        GlobalCommandServerClientContainer.getInstance().removeClient(clientId);
    }

    public void sendCommandToClient(String command){
        if(socket.isClosed()){
            throw new IllegalStateException("Connection was closed, can not send command");
        }

        processThread.writeToClientWithLn(command);
    }

    public String getClientId(){
        return clientId;
    }

}
