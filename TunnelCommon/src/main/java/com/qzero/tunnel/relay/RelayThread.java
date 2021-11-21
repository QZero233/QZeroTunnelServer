package com.qzero.tunnel.relay;

import com.qzero.tunnel.crypto.DataWithLength;
import com.qzero.tunnel.utils.StreamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class RelayThread extends Thread {

    private Socket source;
    private Socket destination;

    private ClientDisconnectedListener listener;

    private DataPreprocessor preprocessor;

    private Logger log= LoggerFactory.getLogger(getClass());

    private boolean isSourceTunnel=false;

    public RelayThread(Socket source, Socket destination, ClientDisconnectedListener listener) {
        this.source = source;
        this.destination = destination;
        this.listener=listener;
    }

    public RelayThread(Socket source, Socket destination, ClientDisconnectedListener listener, DataPreprocessor preprocessor) {
        this.source = source;
        this.destination = destination;
        this.listener = listener;
        this.preprocessor = preprocessor;
    }

    public void setSourceTunnel(boolean sourceTunnel) {
        isSourceTunnel = sourceTunnel;
    }

    @Override
    public void run() {
        super.run();

        try {
            InputStream sourceIs=source.getInputStream();
            OutputStream dstOs=destination.getOutputStream();

            int length=preprocessor.lengthOfReceive();

            if(isSourceTunnel){
                //Read from tunnel
                //The head of package contains the length

                while (true){
                    //TODO handle the exception, make connection lost special
                    int dataLength= StreamUtils.readIntWith4Bytes(sourceIs);//FIXME 关闭连接时可能报 Range [0, 0 + -1) out of bounds for length 4
                    byte[] buf=StreamUtils.readSpecifiedLengthDataFromInputStream(sourceIs,dataLength);

                    DataWithLength data=new DataWithLength(buf,dataLength);
                    if(preprocessor!=null){
                        data=preprocessor.afterReceived(data);
                        data=preprocessor.beforeSent(data);
                    }

                    dstOs.write(data.getData(),0,data.getLength());
                }
            }else {
                if (length <= 0) {
                    //When the length is less or equal than 0
                    //The length is not specified

                    byte[] buf = new byte[102400];
                    int len;
                    while (!isInterrupted()) {
                        len = sourceIs.read(buf);

                        if (len == -1) {
                            break;
                        }

                        DataWithLength data=new DataWithLength(buf,len);
                        if (preprocessor != null) {
                            data = preprocessor.afterReceived(data);
                            data = preprocessor.beforeSent(data);
                        }

                        if(!isSourceTunnel)
                            StreamUtils.writeIntWith4Bytes(dstOs,data.getLength());
                        dstOs.write(data.getData(), 0, data.getLength());
                    }
                } else if (length == 1) {
                    //When length is 1
                    //Just read one byte and write without using array

                    while (!isInterrupted()) {
                        int b = sourceIs.read();

                        //Which means it's the end of the stream
                        if (b == -1) {
                            break;
                        }

                        //Construct byte array to use crypto module
                        byte[] buf = new byte[]{(byte) b};

                        DataWithLength data=new DataWithLength(buf,1);
                        if (preprocessor != null) {
                            data = preprocessor.afterReceived(data);
                            data = preprocessor.beforeSent(data);
                        }

                        if(!isSourceTunnel)
                            StreamUtils.writeIntWith4Bytes(dstOs,data.getLength());
                        dstOs.write(data.getData(), 0, data.getLength());
                    }
                } else {
                    //In this case, we need array

                    byte[] buf = new byte[length];
                    int len;

                    while (!isInterrupted()) {
                        int totalRead = 0;

                        //Read until full
                        while (totalRead < length) {
                            //Read from where it ends last time
                            len = sourceIs.read(buf, totalRead, buf.length - totalRead);

                            //If len=-1, which means some error occurs, pass it down by setting totalRead=-1
                            if (len == -1) {
                                totalRead = -1;
                                break;
                            }

                            totalRead += len;
                        }

                        //Which means some error occurs when reading specified length of data
                        if (totalRead == -1) {
                            break;
                        }

                        DataWithLength data=new DataWithLength(buf,totalRead);
                        if (preprocessor != null) {
                            data = preprocessor.afterReceived(data);
                            data = preprocessor.beforeSent(data);
                        }

                        if(!isSourceTunnel)
                            StreamUtils.writeIntWith4Bytes(dstOs,data.getLength());
                        dstOs.write(data.getData(), 0, data.getLength());
                    }
                }
            }

        }catch (Exception e){
            if(isInterrupted()){
                log.trace(String.format("Relay route from %s to %s has been interrupted",
                        source.getInetAddress().getHostAddress(),destination.getInetAddress().getHostAddress()));
                return;
            }
            log.error(String.format("Relay failed, route from %s to %s has crashed",
                    source.getInetAddress().getHostAddress(),destination.getInetAddress().getHostAddress()),e);
        }

        listener.onDisconnected();
    }
}
