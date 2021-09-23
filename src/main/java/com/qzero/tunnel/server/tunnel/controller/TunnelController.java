package com.qzero.tunnel.server.tunnel.controller;

import com.qzero.tunnel.server.data.ActionResult;
import com.qzero.tunnel.server.data.TunnelConfig;
import com.qzero.tunnel.server.exception.ErrorCodeList;
import com.qzero.tunnel.server.exception.ResponsiveException;
import com.qzero.tunnel.server.exception.TunnelDoesNotExistException;
import com.qzero.tunnel.server.tunnel.TunnelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/tunnel")
public class TunnelController {

    @Autowired
    private TunnelService tunnelService;

    @PutMapping("/{tunnel_port}")
    public ActionResult updateTunnel(@PathVariable("tunnel_port") int tunnelPort,
                                     @RequestParam("local_ip") String localIp,
                                     @RequestParam("local_port") int localPort,
                                     @RequestHeader("username") String username) throws ResponsiveException {

        TunnelConfig tunnelConfig=tunnelService.getTunnelConfig(tunnelPort);
        if(tunnelConfig==null)
            throw new TunnelDoesNotExistException(tunnelPort);

        if(!tunnelConfig.getTunnelOwner().equals(username))
            throw new ResponsiveException(ErrorCodeList.CODE_PERMISSION_DENIED,"You don't own the tunnel");

        tunnelConfig.setLocalIp(localIp);
        tunnelConfig.setLocalPort(localPort);
        tunnelService.updateTunnel(tunnelConfig);

        return new ActionResult(true,null);
    }

    @PostMapping("/{tunnel_port}")
    public ActionResult newTunnel(@PathVariable("tunnel_port") int tunnelPort,
                                  @RequestParam("local_ip") String localIp,
                                  @RequestParam("local_port") int localPort,
                                  @RequestHeader("username") String username) throws ResponsiveException{

        tunnelService.newTunnel(new TunnelConfig(tunnelPort,username,localIp,localPort));
        return new ActionResult(true,null);
    }

    @RequestMapping("/{tunnel_port}/open")
    public ActionResult openTunnel(@PathVariable("tunnel_port") int tunnelPort,
                                   @RequestHeader("username") String username) throws ResponsiveException, IOException {

        TunnelConfig tunnelConfig=tunnelService.getTunnelConfig(tunnelPort);
        if(tunnelConfig==null)
            throw new TunnelDoesNotExistException(tunnelPort);

        if(!tunnelConfig.getTunnelOwner().equals(username))
            throw new ResponsiveException(ErrorCodeList.CODE_PERMISSION_DENIED,"You don't own the tunnel");

        tunnelService.openTunnel(tunnelPort);

        return new ActionResult(true,null);
    }

    @RequestMapping("/{tunnel_port}/close")
    public ActionResult closeTunnel(@PathVariable("tunnel_port") int tunnelPort,
                                   @RequestHeader("username") String username) throws ResponsiveException, IOException {

        TunnelConfig tunnelConfig=tunnelService.getTunnelConfig(tunnelPort);
        if(tunnelConfig==null)
            throw new TunnelDoesNotExistException(tunnelPort);

        if(!tunnelConfig.getTunnelOwner().equals(username))
            throw new ResponsiveException(ErrorCodeList.CODE_PERMISSION_DENIED,"You don't own the tunnel");

        tunnelService.closeTunnel(tunnelPort);

        return new ActionResult(true,null);
    }

}
