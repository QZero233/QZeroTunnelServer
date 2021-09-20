package com.qzero.tunnel.server.authorize;

import com.qzero.tunnel.server.data.ActionResult;
import com.qzero.tunnel.server.data.TunnelUser;
import com.qzero.tunnel.server.exception.ResponsiveException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthorizeService authorizeHelper;

    @PostMapping("/login")
    public ActionResult login(@RequestParam("username") String username,
                              @RequestParam("password_hash") String passwordHash) throws ResponsiveException {

        String tokenId= authorizeHelper.login(new TunnelUser(username,passwordHash));
        return new ActionResult(true,tokenId);
    }

}
