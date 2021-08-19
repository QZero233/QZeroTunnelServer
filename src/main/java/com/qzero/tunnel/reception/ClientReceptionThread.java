package com.qzero.tunnel.reception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class ClientReceptionThread extends Thread {

    //If the inputStream starts with 12 34 56, we'll take it as host connection and take commands
    //Otherwise, it's regarded as client, no command will be taken

    private Logger log= LoggerFactory.getLogger(getClass());

    private Socket socket;
    private InputStream is;

    private TunnelServerThread tunnelThread;

    public ClientReceptionThread(Socket socket,TunnelServerThread tunnelThread) throws IOException {
        this.socket = socket;
        this.tunnelThread=tunnelThread;
        is=socket.getInputStream();
    }

    @Override
    public void run() {
        super.run();

        byte[] buf=new byte[3];
        try {
            buf[0]= (byte) is.read();
            buf[1]= (byte) is.read();
            buf[2]= (byte) is.read();
        } catch (IOException e) {
            log.error("Failed to read identify code",e);
        }

        if(buf[0]==12 && buf[1]==34 && buf[2]==56){
            new HostCommandThread(socket,tunnelThread).start();
        }else{
            if(!tunnelThread.isHostConnected()){
                log.debug("Client connection closed due to no connected host");
                try {
                    socket.close();
                } catch (IOException e) {

                }
                return;
            }

            try {
                tunnelThread.addClientConnection(socket,buf);
            } catch (IOException e) {
                try {
                    socket.getOutputStream().write("Lost connection with host\n".getBytes());
                    socket.close();
                } catch (IOException ioException) {

                }
            }
        }


    }
}
