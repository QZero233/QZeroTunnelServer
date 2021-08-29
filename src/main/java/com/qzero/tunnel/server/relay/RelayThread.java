package com.qzero.tunnel.server.relay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class RelayThread extends Thread {

    private Socket source;
    private Socket destination;

    private ClientDisconnectedListener listener;

    private Logger log= LoggerFactory.getLogger(getClass());

    public RelayThread(Socket source, Socket destination, ClientDisconnectedListener listener) {
        this.source = source;
        this.destination = destination;
        this.listener=listener;
    }

    @Override
    public void run() {
        super.run();

        try {
            InputStream sourceIs=source.getInputStream();
            OutputStream dstOs=destination.getOutputStream();

            byte[] buf=new byte[102400];
            int len;
            while (!isInterrupted()){
                len=sourceIs.read(buf);
                if(len==-1){
                    break;
                }
                dstOs.write(buf,0,len);
            }
        }catch (Exception e){
            if(isInterrupted()){
                log.trace(String.format("Relay route from %s to %s has been interrupted",
                        source.getInetAddress().getHostAddress(),destination.getInetAddress().getHostAddress()));
                return;
            }
            log.trace(String.format("Relay failed, route from %s to %s has crashed",
                    source.getInetAddress().getHostAddress(),destination.getInetAddress().getHostAddress()),e);
        }

        listener.onDisconnected();
    }
}
