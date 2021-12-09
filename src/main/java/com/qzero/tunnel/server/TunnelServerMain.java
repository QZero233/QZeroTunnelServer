package com.qzero.tunnel.server;

import com.qzero.tunnel.server.config.PortConfig;
import com.qzero.tunnel.server.relay.reception.RelayServerReceptionThread;
import com.qzero.tunnel.server.relay.remind.RemindServerReceptionThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class TunnelServerMain implements ApplicationRunner {

    private static Logger log= LoggerFactory.getLogger(TunnelServerMain.class);

    public static void main(String[] args) {
        new SpringApplicationBuilder(TunnelServerMain.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }


    @Override
    public void run(ApplicationArguments args){
        log.info("Loading config");
        PortConfig serverConfig=SpringUtil.getBean(PortConfig.class);

        log.info("Starting remind server");
        try {
            new RemindServerReceptionThread(serverConfig.getRemindServerPort()).start();
        }catch (Exception e){
            log.error("Failed to start remind server, program is shutting down",e);
            System.exit(0);
        }

        log.info("Starting relay reception server");
        try {
            new RelayServerReceptionThread(serverConfig.getReceptionServerPort()).start();
        }catch (Exception e){
            log.error("Failed to start relay reception server, program is shutting down",e);
            System.exit(0);
        }

        log.info("Tunnel server has started successfully");
    }
}
