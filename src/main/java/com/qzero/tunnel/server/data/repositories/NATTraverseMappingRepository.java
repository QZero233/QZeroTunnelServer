package com.qzero.tunnel.server.data.repositories;

import com.qzero.tunnel.server.data.NATTraverseMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NATTraverseMappingRepository extends JpaRepository<NATTraverseMapping,Integer> {


}

