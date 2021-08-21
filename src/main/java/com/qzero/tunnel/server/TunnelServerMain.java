package com.qzero.tunnel.server;

import com.qzero.tunnel.server.command.CommandServerReceptionThread;
import com.qzero.tunnel.server.relay.RelayServerReceptionThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TunnelServerMain {

    private static Logger log= LoggerFactory.getLogger(TunnelServerMain.class);

    private static final int COMMAND_SERVER_PORT=9999;//TODO config by file
    private static final int RELAY_RECEPTION_SERVER_PORT=9998;//TODO config by file

    public static void main(String[] args) {
        log.info("Starting command server");

        try {
            new CommandServerReceptionThread(COMMAND_SERVER_PORT).start();
        }catch (Exception e){
            log.error("Failed to start command server, program is shutting down",e);
            System.exit(0);
        }

        log.info("Starting relay reception server");
        try {
            new RelayServerReceptionThread(RELAY_RECEPTION_SERVER_PORT).start();
        }catch (Exception e){
            log.error("Failed to start relay reception server, program is shutting down",e);
            System.exit(0);
        }

        log.info("Tunnel server has started successfully");
    }

}
