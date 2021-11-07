package com.qzero.tunnel.server.traverse.service;

import com.qzero.tunnel.server.data.NATTraverseMapping;
import com.qzero.tunnel.server.data.repositories.NATTraverseMappingRepository;
import com.qzero.tunnel.server.exception.ErrorCodeList;
import com.qzero.tunnel.server.exception.ResponsiveException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NATTraverseMappingService {

    private final NATTraverseMappingRepository traverseMappingRepository;

    public NATTraverseMappingService(NATTraverseMappingRepository traverseMappingRepository) {
        this.traverseMappingRepository = traverseMappingRepository;
    }

    public void newNATTraverseMapping(NATTraverseMapping natTraverseMapping) throws ResponsiveException {
        if(traverseMappingRepository.existsById(natTraverseMapping.getTunnelPort()))
            throw new ResponsiveException(ErrorCodeList.CODE_BAD_REQUEST_PARAMETER,
                    String.format("Tunnel on port %d already has a nat traverse mapping", natTraverseMapping.getTunnelPort()));
        traverseMappingRepository.save(natTraverseMapping);
    }

    public void updateNATTraverseMapping(NATTraverseMapping natTraverseMapping) throws ResponsiveException {
        if(!traverseMappingRepository.existsById(natTraverseMapping.getTunnelPort()))
            throw new ResponsiveException(ErrorCodeList.CODE_MISSING_RESOURCE,
                    String.format("NAT traverse mapping attached to tunnel port %d does not exist", natTraverseMapping.getTunnelPort()));

        traverseMappingRepository.save(natTraverseMapping);
    }

    public boolean checkExistenceByTunnelPort(int tunnelPort){
        return traverseMappingRepository.existsById(tunnelPort);
    }

    public NATTraverseMapping getNATTraverseMapping(int tunnelPort){
       return traverseMappingRepository.getById(tunnelPort);
    }

    public void deleteNATTraverseMapping(int tunnelPort) throws ResponsiveException {
        if(!traverseMappingRepository.existsById(tunnelPort))
            throw new ResponsiveException(ErrorCodeList.CODE_MISSING_RESOURCE,
                    String.format("NAT traverse mapping attached to tunnel port %d does not exist", tunnelPort));
        traverseMappingRepository.deleteById(tunnelPort);
    }

}
