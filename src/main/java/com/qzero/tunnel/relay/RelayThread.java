package com.qzero.tunnel.relay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class RelayThread extends Thread {

    private byte[] preSentBytes;
    private Socket source;
    private Socket destination;

    private ClientDisconnectedListener listener;

    private Logger log= LoggerFactory.getLogger(getClass());

    public RelayThread(byte[] preSentBytes, Socket source, Socket destination, ClientDisconnectedListener listener) {
        this.preSentBytes = preSentBytes;
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

            if(preSentBytes!=null){
                dstOs.write(preSentBytes);
            }

            byte[] buf=new byte[102400];
            int len;
            while (true){
                len=sourceIs.read(buf);
                if(len==-1){
                    break;
                }
                dstOs.write(buf,0,len);
            }
        }catch (Exception e){
            log.debug("Route failed",e);
        }

        listener.onDisconnected();

    }
}
