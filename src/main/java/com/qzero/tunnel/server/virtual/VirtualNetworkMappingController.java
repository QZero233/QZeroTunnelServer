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
    public ActionResult newNATTraverseMapping(@RequestParam("dst_identity") String dstIdentity,
                                              @RequestParam("dst_username") String dstUsername) throws ResponsiveException {
        service.addMapping(new VirtualNetworkMapping(dstIdentity,dstUsername));
        return new ActionResult(true,null);
    }

    @DeleteMapping("/{dst_identity}")
    public ActionResult deleteNATTraverseMapping(@PathVariable("dst_identity") String dstIdentity) throws ResponsiveException {
        service.deleteMapping(dstIdentity);
        return new ActionResult(true,null);
    }

    @PutMapping("/{dst_identity}")
    public ActionResult updateNATTraverseMapping(@PathVariable("dst_identity") String dstIdentity,
                                                    @RequestParam("dst_username") String dstUsername) throws ResponsiveException {
        service.updateMapping(new VirtualNetworkMapping(dstIdentity,dstUsername));
        return new ActionResult(true,null);
    }

}
