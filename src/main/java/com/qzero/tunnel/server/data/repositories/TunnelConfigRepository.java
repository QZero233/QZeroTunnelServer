package com.qzero.tunnel.server.data.repositories;

import com.qzero.tunnel.server.data.TunnelConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TunnelConfigRepository extends JpaRepository<TunnelConfig,Integer> {

    boolean existsByTunnelPort(int tunnelPort);

    TunnelConfig getByTunnelPort(int tunnelPort);

    List<TunnelConfig> findAllByTunnelOwner(String tunnelOwner);

}
