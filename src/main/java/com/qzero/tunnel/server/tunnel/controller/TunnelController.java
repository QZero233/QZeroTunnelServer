package com.qzero.tunnel.server.tunnel.controller;

import com.qzero.tunnel.server.data.ActionResult;
import com.qzero.tunnel.server.data.TunnelConfig;
import com.qzero.tunnel.server.exception.ErrorCodeList;
import com.qzero.tunnel.server.exception.ResponsiveException;
import com.qzero.tunnel.server.exception.TunnelDoesNotExistException;
import com.qzero.tunnel.server.tunnel.TunnelService;
import com.qzero.tunnel.server.utils.JsonUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/tunnel")
public class TunnelController {

    @Autowired
    private TunnelService tunnelService;

    /*@PutMapping("/{tunnel_port}")
    public ActionResult updateTunnel(@PathVariable("tunnel_port") int tunnelPort,
                                     @RequestParam("local_ip") String localIp,
                                     @RequestParam("local_port") int localPort,
                                     @RequestParam("crypto_module_name") String cryptoModuleName,
                                     @RequestHeader("username") String username) throws ResponsiveException {

        TunnelConfig tunnelConfig=tunnelService.getTunnelConfig(tunnelPort);
        if(tunnelConfig==null)
            throw new TunnelDoesNotExistException(tunnelPort);

        if(!tunnelConfig.getTunnelOwner().equals(username))
            throw new ResponsiveException(ErrorCodeList.CODE_PERMISSION_DENIED,"You don't own the tunnel");

        tunnelConfig.setLocalIp(localIp);
        tunnelConfig.setLocalPort(localPort);
        tunnelConfig.setCryptoModuleName(cryptoModuleName);
        tunnelService.updateTunnel(tunnelConfig);

        return new ActionResult(true,null);
    }*/
    //TODO make it partial update
    //TODO add nat traverse mapping update

    @PostMapping("/{tunnel_port}")
    public ActionResult newTunnel(@PathVariable("tunnel_port") int tunnelPort,
                                  @RequestParam("crypto_module_name") String cryptoModuleName,
                                  @RequestParam("tunnel_type") int tunnelType,
                                  @RequestHeader("username") String username) throws ResponsiveException{

        tunnelService.newTunnel(new TunnelConfig(tunnelPort,username,cryptoModuleName,tunnelType));
        return new ActionResult(true,null);
    }

    @DeleteMapping ("/{tunnel_port}")
    public ActionResult deleteTunnel(@PathVariable("tunnel_port") int tunnelPort,
                                     @RequestHeader("username") String username) throws ResponsiveException {
        TunnelConfig tunnelConfig=tunnelService.getTunnelConfig(tunnelPort);
        if(tunnelConfig==null)
            throw new TunnelDoesNotExistException(tunnelPort);

        if(!tunnelConfig.getTunnelOwner().equals(username))
            throw new ResponsiveException(ErrorCodeList.CODE_PERMISSION_DENIED,"You don't own the tunnel");

        tunnelService.deleteTunnel(tunnelPort);
        return new ActionResult(true,null);
    }

    @RequestMapping("/{tunnel_port}/open")
    public ActionResult openTunnel(@PathVariable("tunnel_port") int tunnelPort,
                                   @RequestHeader("username") String username) throws Exception {

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
                                   @RequestHeader("username") String username) throws Exception {

        TunnelConfig tunnelConfig=tunnelService.getTunnelConfig(tunnelPort);
        if(tunnelConfig==null)
            throw new TunnelDoesNotExistException(tunnelPort);

        if(!tunnelConfig.getTunnelOwner().equals(username))
            throw new ResponsiveException(ErrorCodeList.CODE_PERMISSION_DENIED,"You don't own the tunnel");

        tunnelService.closeTunnel(tunnelPort);

        return new ActionResult(true,null);
    }

    @GetMapping("/{tunnel_port}")
    public ActionResult getTunnelConfig(@PathVariable("tunnel_port") int tunnelPort,
                                   @RequestHeader("username") String username) throws ResponsiveException{
        TunnelConfig tunnelConfig=tunnelService.getTunnelConfig(tunnelPort);
        if(tunnelConfig==null)
            throw new TunnelDoesNotExistException(tunnelPort);

        if(!tunnelConfig.getTunnelOwner().equals(username))
            throw new ResponsiveException(ErrorCodeList.CODE_PERMISSION_DENIED,"You don't own the tunnel");

        tunnelConfig= (TunnelConfig) Hibernate.unproxy(tunnelConfig);
        return new ActionResult(true, JsonUtils.toJson(tunnelConfig));
    }

    @GetMapping("/")
    public ActionResult getAllTunnelConfig(@RequestHeader("username") String username) {
        List<TunnelConfig> configList=tunnelService.getAllTunnelConfigByOwner(username);
        return new ActionResult(true,JsonUtils.toJson(configList));
    }

}
