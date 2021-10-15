package com.qzero.tunnel.server.data.repositories;

import com.qzero.tunnel.server.data.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTokenRepository extends JpaRepository<UserToken,String> {

    UserToken findByTokenIdAndUsername(String tokenId,String username);

    boolean existsByTokenIdAndUsername(String tokenId,String username);

}
