package com.qzero.tunnel.server.authorize;

import com.qzero.tunnel.server.data.TunnelUser;
import com.qzero.tunnel.server.data.UserToken;
import com.qzero.tunnel.server.data.repositories.TunnelUserRepository;
import com.qzero.tunnel.server.data.repositories.UserTokenRepository;
import com.qzero.tunnel.server.exception.ErrorCodeList;
import com.qzero.tunnel.server.exception.ResponsiveException;
import com.qzero.tunnel.server.utils.UUIDUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class AuthorizeService {

    @Autowired
    private TunnelUserRepository userRepository;

    @Autowired
    private UserTokenRepository tokenRepository;

    /**
     * Login for a user
     * @param user
     * @return the user's token
     */
    public String login(TunnelUser user) throws ResponsiveException {
        if(!userRepository.existsByUsernameAndPasswordHash(user.getUsername(),user.getPasswordHash())){
            throw new ResponsiveException(ErrorCodeList.CODE_WRONG_LOGIN_INFO,"Login failed");
        }

        UserToken token=new UserToken(UUIDUtils.getRandomUUID(),user.getUsername());
        tokenRepository.save(token);
        return token.getTokenId();
    }

    public TunnelUser getUserByToken(String tokenId) throws ResponsiveException {
        UserToken token=tokenRepository.getById(tokenId);
        if(token==null){
            throw new ResponsiveException(ErrorCodeList.CODE_ILLEGAL_TOKEN,
                    String.format("Token with id %s does not exist", tokenId));
        }

        String username=token.getUsername();
        TunnelUser user=userRepository.getByUsername(username);
        if(user==null){
            throw new ResponsiveException(ErrorCodeList.CODE_MISSING_RESOURCE,
                    String.format("User with username %s does not exist", username));
        }

        return user;
    }

    public void addUser(TunnelUser user) throws ResponsiveException {
        if(userRepository.existsById(user.getUsername())){
            throw new ResponsiveException(ErrorCodeList.CODE_BAD_REQUEST_PARAMETER,
                    String.format("User named %s already exists", user.getUsername()));
        }

        userRepository.save(user);
    }

    public boolean checkTokenValidity(UserToken token){
        return tokenRepository.existsByTokenIdAndUsername(token.getTokenId(),token.getUsername());
    }

}
