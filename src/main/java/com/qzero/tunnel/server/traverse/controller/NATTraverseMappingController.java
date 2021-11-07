package com.qzero.tunnel.server.traverse.controller;

import com.qzero.tunnel.server.data.ActionResult;
import com.qzero.tunnel.server.data.NATTraverseMapping;
import com.qzero.tunnel.server.exception.ErrorCodeList;
import com.qzero.tunnel.server.exception.ResponsiveException;
import com.qzero.tunnel.server.traverse.service.NATTraverseMappingService;
import com.qzero.tunnel.server.tunnel.TunnelService;
import com.qzero.tunnel.server.utils.JsonUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/nat_traverse_mapping")
public class NATTraverseMappingController {

    @Autowired
    private NATTraverseMappingService natTraverseMappingService;

    @Autowired
    private TunnelService tunnelService;

    @PostMapping("/{tunnel_port}")
    public ActionResult newNATTraverseMapping(@PathVariable("tunnel_port") int tunnelPort,
                                              @RequestParam("local_ip") String localIp,
                                              @RequestParam("local_port") int localPort,
                                              @RequestHeader("username") String username) throws ResponsiveException {

        tunnelService.checkTunnelExistenceAndPermission(tunnelPort,username);

        NATTraverseMapping natTraverseMapping=new NATTraverseMapping(tunnelPort,localIp,localPort);
        natTraverseMappingService.newNATTraverseMapping(natTraverseMapping);

        return new ActionResult(true,null);
    }

    @DeleteMapping("/{tunnel_port}")
    public ActionResult deleteNATTraverseMapping(@PathVariable("tunnel_port") int tunnelPort,
                                                 @RequestHeader("username") String username) throws ResponsiveException {

        tunnelService.checkTunnelExistenceAndPermission(tunnelPort,username);

        if(!natTraverseMappingService.checkExistenceByTunnelPort(tunnelPort)){
            throw new ResponsiveException(ErrorCodeList.CODE_MISSING_RESOURCE,
                    String.format("NAT traverse mapping attached to tunnel port %d does not exist", tunnelPort));
        }

        natTraverseMappingService.deleteNATTraverseMapping(tunnelPort);

        return new ActionResult(true,null);
    }

    @PutMapping("/{tunnel_port}/hot")
    public ActionResult updateNATTraverseMappingHot(@PathVariable("tunnel_port") int tunnelPort,
                                                    @RequestParam("local_ip") String localIp,
                                                    @RequestParam("local_port") int localPort,
                                                    @RequestHeader("username") String username) throws ResponsiveException {

        tunnelService.checkTunnelExistenceAndPermission(tunnelPort,username);

        if(!natTraverseMappingService.checkExistenceByTunnelPort(tunnelPort)){
            throw new ResponsiveException(ErrorCodeList.CODE_MISSING_RESOURCE,
                    String.format("NAT traverse mapping attached to tunnel port %d does not exist", tunnelPort));
        }

        NATTraverseMapping natTraverseMapping=natTraverseMappingService.getNATTraverseMapping(tunnelPort);
        natTraverseMapping.setLocalIp(localIp);
        natTraverseMapping.setLocalPort(localPort);
        natTraverseMappingService.updateNATTraverseMapping(natTraverseMapping);

        return new ActionResult(true,null);
    }

    @PutMapping("/{tunnel_port}")
    public ActionResult updateNATTraverseMapping(@PathVariable("tunnel_port") int tunnelPort,
                                                    @RequestParam("local_ip") String localIp,
                                                    @RequestParam("local_port") int localPort,
                                                    @RequestHeader("username") String username) throws ResponsiveException {
        if(tunnelService.isTunnelRunning(tunnelPort)){
            throw new ResponsiveException(ErrorCodeList.CODE_BAD_REQUEST_PARAMETER,"Can not update cryptoModule when tunnel is running");
        }

        return updateNATTraverseMappingHot(tunnelPort,localIp,localPort,username);
    }

    @GetMapping("/{tunnel_port}")
    public ActionResult getNATTraverseMapping(@PathVariable("tunnel_port") int tunnelPort,
                                              @RequestHeader("username") String username) throws ResponsiveException {

        tunnelService.checkTunnelExistenceAndPermission(tunnelPort,username);

        NATTraverseMapping natTraverseMapping=natTraverseMappingService.getNATTraverseMapping(tunnelPort);

        if(natTraverseMapping==null)
            throw new ResponsiveException(ErrorCodeList.CODE_MISSING_RESOURCE,
                    String.format("NAT traverse mapping attached to tunnel port %d does not exist", tunnelPort));

        natTraverseMapping= (NATTraverseMapping) Hibernate.unproxy(natTraverseMapping);

        return new ActionResult(true, JsonUtils.toJson(natTraverseMapping));
    }

}
