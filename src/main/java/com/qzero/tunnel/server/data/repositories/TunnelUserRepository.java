package com.qzero.tunnel.server.data.repositories;

import com.qzero.tunnel.server.data.TunnelUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TunnelUserRepository extends JpaRepository<TunnelUser,String> {

    boolean existsByUsernameAndPasswordHash(String username,String passwordHash);

    TunnelUser getByUsername(String username);

}
