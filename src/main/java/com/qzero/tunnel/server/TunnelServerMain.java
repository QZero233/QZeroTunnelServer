package com.qzero.tunnel.server;

import com.qzero.tunnel.server.command.CommandServerReceptionThread;
import com.qzero.tunnel.server.config.GlobalConfigurationManager;
import com.qzero.tunnel.server.config.ServerConfiguration;
import com.qzero.tunnel.server.relay.RelayServerReceptionThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class TunnelServerMain {

    private static Logger log= LoggerFactory.getLogger(TunnelServerMain.class);

    public static void main(String[] args) {
        log.info("Loading config");
        try {
            loadConfig();
        }catch (Exception e){
            log.error("Failed to load config, program is shutting down",e);
            return;
        }

        GlobalConfigurationManager configurationManager=GlobalConfigurationManager.getInstance();
        ServerConfiguration serverConfiguration=configurationManager.getServerConfiguration();
        log.info("Starting command server");
        try {
            new CommandServerReceptionThread(serverConfiguration.getCommandServerPort()).start();
        }catch (Exception e){
            log.error("Failed to start command server, program is shutting down",e);
            System.exit(0);
        }

        log.info("Starting relay reception server");
        try {
            new RelayServerReceptionThread(serverConfiguration.getReceptionServerPort()).start();
        }catch (Exception e){
            log.error("Failed to start relay reception server, program is shutting down",e);
            System.exit(0);
        }

        log.info("Tunnel server has started successfully");
    }

    private static void loadConfig() throws IOException, InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        GlobalConfigurationManager configurationManager=GlobalConfigurationManager.getInstance();

        log.info("Loading server config");
        configurationManager.loadServerConfig();
        log.info("Loaded server config");
    }

}
