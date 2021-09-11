package com.qzero.tunnel.server.authorize;

import com.qzero.tunnel.server.SpringUtil;
import com.qzero.tunnel.server.data.TunnelUser;
import com.qzero.tunnel.server.data.repositories.TunnelUserRepository;

import java.io.IOException;

public class AuthorizeHelper {

    private static AuthorizeHelper instance;

    private TunnelUserRepository userRepository;

    public static AuthorizeHelper getInstance(){
        if(instance==null)
            instance=new AuthorizeHelper();
        return instance;
    }

    private AuthorizeHelper(){
        userRepository=SpringUtil.getBean(TunnelUserRepository.class);
    }

    public boolean checkAuthorize(TunnelUser user) throws IOException {
        return userRepository.existsByUsernameAndPasswordHash(user.getUsername(),user.getPasswordHash());
    }

    public TunnelUser getUser(String username) throws UserDoesNotExistException, IOException {
        return userRepository.getByUsername(username);
    }

}
