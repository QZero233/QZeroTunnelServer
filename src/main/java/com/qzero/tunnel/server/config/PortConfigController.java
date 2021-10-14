package com.qzero.tunnel.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PortConfigController {

    @Autowired
    private PortConfig serverConfig;

    @RequestMapping("/server/port_info")
    public ServerPortInfo getServerPortInfo(){
        return new ServerPortInfo(serverConfig.getRemindServerPort(),serverConfig.getReceptionServerPort());
    }

}
