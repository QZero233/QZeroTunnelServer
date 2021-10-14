package com.qzero.tunnel.server.authorize;

import com.qzero.tunnel.server.data.UserToken;
import com.qzero.tunnel.server.data.repositories.UserTokenRepository;
import com.qzero.tunnel.server.exception.ErrorCodeList;
import com.qzero.tunnel.server.exception.ResponsiveException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Transactional
@Component
public class TokenCheckInterceptor implements HandlerInterceptor {

    private Logger log= LoggerFactory.getLogger(getClass());

    @Autowired
    private UserTokenRepository tokenRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(request.getServletPath().startsWith("/auth") || request.getServletPath().startsWith("/server")){
            return true;
        }

        String tokenId=request.getHeader("token_id");
        String userName=request.getHeader("username");

        if(tokenId==null || userName==null)
            throw new ResponsiveException(ErrorCodeList.CODE_BAD_REQUEST_PARAMETER,"TokenId and OwnerUserName can not be empty");

        if(!tokenRepository.existsById(tokenId))
            throw new ResponsiveException(ErrorCodeList.CODE_ILLEGAL_TOKEN,"Token does not exist");

        UserToken token=tokenRepository.getById(tokenId);

        if(!token.getUsername().equals(userName))
            throw new ResponsiveException(ErrorCodeList.CODE_ILLEGAL_TOKEN,"Token owner name does not match");

        log.trace("Token valid");

        return true;
    }
}
