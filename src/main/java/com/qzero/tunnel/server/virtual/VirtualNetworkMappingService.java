package com.qzero.tunnel.server.virtual;

import com.qzero.tunnel.server.data.VirtualNetworkMapping;
import com.qzero.tunnel.server.data.repositories.VirtualNetworkMappingRepository;
import com.qzero.tunnel.server.exception.ResponsiveException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class VirtualNetworkMappingService {

    @Autowired
    private VirtualNetworkMappingRepository repository;

    public String findDstUser(String dstIdentity) throws ResponsiveException {
        if(!repository.existsById(dstIdentity))
            throw new MissingIdentityException(dstIdentity);

        VirtualNetworkMapping mapping=repository.getById(dstIdentity);
        return mapping.getDstUserName();
    }

    public void addMapping(VirtualNetworkMapping mapping) throws ResponsiveException{
        if(repository.existsById(mapping.getDstIdentity()))
            throw new IdentityConflictException(mapping.getDstIdentity());

        repository.save(mapping);
    }

    public void deleteMapping(String dstIdentity) throws ResponsiveException{
        if(!repository.existsById(dstIdentity))
            throw new MissingIdentityException(dstIdentity);

        repository.deleteById(dstIdentity);
    }

    public void updateMapping(VirtualNetworkMapping mapping) throws ResponsiveException{
        if(!repository.existsById(mapping.getDstIdentity()))
            throw new MissingIdentityException(mapping.getDstIdentity());

        repository.save(mapping);
    }

}
