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
                                              @RequestBody NATTraverseMapping natTraverseMapping,
                                              @RequestHeader("username") String username) throws ResponsiveException {

        tunnelService.checkTunnelExistenceAndPermission(tunnelPort,username);

        natTraverseMapping.setTunnelPort(tunnelPort);
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

    @PutMapping("/{tunnel_port}")
    public ActionResult updateNATTraverseMapping(@PathVariable("tunnel_port") int tunnelPort,
                                                 @RequestBody NATTraverseMapping natTraverseMapping,
                                                    @RequestHeader("username") String username) throws ResponsiveException {

        tunnelService.checkTunnelExistenceAndPermission(tunnelPort,username);

        if(!natTraverseMappingService.checkExistenceByTunnelPort(tunnelPort)){
            throw new ResponsiveException(ErrorCodeList.CODE_MISSING_RESOURCE,
                    String.format("NAT traverse mapping attached to tunnel port %d does not exist", tunnelPort));
        }

        NATTraverseMapping natTraverseMappingOld=natTraverseMappingService.getNATTraverseMapping(tunnelPort);
        natTraverseMappingOld.setLocalIp(natTraverseMapping.getLocalIp());
        natTraverseMappingOld.setLocalPort(natTraverseMapping.getLocalPort());
        natTraverseMappingService.updateNATTraverseMapping(natTraverseMappingOld);

        return new ActionResult(true,null);
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
