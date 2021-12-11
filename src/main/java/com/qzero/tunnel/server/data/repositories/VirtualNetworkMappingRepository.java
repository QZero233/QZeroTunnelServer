package com.qzero.tunnel.server.data.repositories;

import com.qzero.tunnel.server.data.VirtualNetworkMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VirtualNetworkMappingRepository extends JpaRepository<VirtualNetworkMapping,String> {
}
