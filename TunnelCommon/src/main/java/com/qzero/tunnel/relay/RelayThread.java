package com.qzero.tunnel.relay;

import com.qzero.tunnel.crypto.DataWithLength;
import com.qzero.tunnel.utils.StreamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

public class RelayThread extends Thread {

    private Socket source;
    private Socket destination;

    private ClientDisconnectedListener listener;

    private DataPreprocessor preprocessor;

    private Logger log = LoggerFactory.getLogger(getClass());

    private RelayStrategy relayStrategy=new RelayStrategy(true,true);

    public RelayThread(Socket source, Socket destination, ClientDisconnectedListener listener) {
        this.source = source;
        this.destination = destination;
        this.listener = listener;
    }

    public RelayThread(Socket source, Socket destination, ClientDisconnectedListener listener, DataPreprocessor preprocessor) {
        this.source = source;
        this.destination = destination;
        this.listener = listener;
        this.preprocessor = preprocessor;
    }

    public void setRelayStrategy(RelayStrategy relayStrategy) {
        this.relayStrategy = relayStrategy;
    }

    @Override
    public void run() {
        super.run();

        try {
            InputStream sourceIs = source.getInputStream();
            OutputStream dstOs = destination.getOutputStream();

            if (!relayStrategy.isDirectlyRead()) {
                //The head of package contains the length

                while (true) {
                    int dataLength = StreamUtils.readIntWith4Bytes(sourceIs);
                    byte[] buf = StreamUtils.readSpecifiedLengthDataFromInputStream(sourceIs, dataLength);

                    DataWithLength data = new DataWithLength(buf, dataLength);
                    writeDataToDst(data, dstOs);
                }
            } else {
                //When the length is less or equal than 0
                //The length is not specified
                byte[] buf = new byte[102400];
                int len;
                while (!isInterrupted()) {
                    len = sourceIs.read(buf);

                    if (len == -1) {
                        break;
                    }

                    DataWithLength data = new DataWithLength(buf, len);
                    writeDataToDst(data, dstOs);
                }
            }

        } catch (SocketException socketException) {
            log.trace(String.format("Relay route from %s to %s has been interrupted due to connection lost",
                    source.getInetAddress().getHostAddress(), destination.getInetAddress().getHostAddress()));
        } catch (IOException ioException) {
            log.trace(String.format("Relay route from %s to %s has been interrupted due to connection lost",
                    source.getInetAddress().getHostAddress(), destination.getInetAddress().getHostAddress()));
        } catch (Exception e) {
            if (isInterrupted()) {
                log.trace(String.format("Relay route from %s to %s has been interrupted",
                        source.getInetAddress().getHostAddress(), destination.getInetAddress().getHostAddress()));
                return;
            }
            log.error(String.format("Relay failed, route from %s to %s has crashed",
                    source.getInetAddress().getHostAddress(), destination.getInetAddress().getHostAddress()), e);
        }

        listener.onDisconnected();
    }

    /**
     * Let data go through preprocessor
     * and send length
     * then send data
     *
     * @param data
     */
    private void writeDataToDst(DataWithLength data, OutputStream dstOs) throws Exception {
        if (preprocessor != null) {
            data = preprocessor.afterReceived(data);
            data = preprocessor.beforeSent(data);
        }

        if (data == null) {
            //Which means some crypto error occurs
            //In respect of the completeness of data
            //We will close this relay session

            throw new Exception("Relay session is forced to close due to crypto error");
        }

        if (!relayStrategy.isDirectlySend())
            StreamUtils.writeIntWith4Bytes(dstOs, data.getLength());
        dstOs.write(data.getData(), 0, data.getLength());
    }
}
