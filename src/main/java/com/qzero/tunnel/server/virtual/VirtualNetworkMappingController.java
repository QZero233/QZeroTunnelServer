package com.qzero.tunnel.server.virtual;

import com.qzero.tunnel.server.data.ActionResult;
import com.qzero.tunnel.server.data.VirtualNetworkMapping;
import com.qzero.tunnel.server.exception.ResponsiveException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/virtual_network_mapping")
public class VirtualNetworkMappingController {

    @Autowired
    private VirtualNetworkMappingService service;

    @PostMapping("/")
    public ActionResult newNATTraverseMapping(@RequestBody VirtualNetworkMapping virtualNetworkMapping) throws ResponsiveException {
        service.addMapping(virtualNetworkMapping);
        return new ActionResult(true,null);
    }

    @DeleteMapping("/{dst_identity}")
    public ActionResult deleteNATTraverseMapping(@PathVariable("dst_identity") String dstIdentity) throws ResponsiveException {
        service.deleteMapping(dstIdentity);
        return new ActionResult(true,null);
    }

    @PutMapping("/{dst_identity}")
    public ActionResult updateNATTraverseMapping(@PathVariable("dst_identity") String dstIdentity,
                                                 @RequestBody VirtualNetworkMapping virtualNetworkMapping) throws ResponsiveException {
        virtualNetworkMapping.setDstIdentity(dstIdentity);
        service.updateMapping(virtualNetworkMapping);
        return new ActionResult(true,null);
    }

}
