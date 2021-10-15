package com.qzero.tunnel.server.authorize;

import com.qzero.tunnel.server.data.ActionResult;
import com.qzero.tunnel.server.data.TunnelUser;
import com.qzero.tunnel.server.data.UserToken;
import com.qzero.tunnel.server.exception.ResponsiveException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/register")
    public ActionResult register(@RequestParam("username") String username,
                                 @RequestParam("password_hash") String passwordHash) throws ResponsiveException {
        authorizeHelper.addUser(new TunnelUser(username,passwordHash));

        return new ActionResult(true,null);
    }

    @GetMapping("/{token}/validity")
    public ActionResult checkTokenValidity(@PathVariable("token") String token,
                                           @RequestParam("username") String username){
        return new ActionResult(authorizeHelper.checkTokenValidity(new UserToken(token,username)),null);
    }

}
