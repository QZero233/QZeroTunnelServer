package com.qzero.tunnel.server.authorize;

import com.qzero.tunnel.server.utils.StreamUtils;

import java.io.File;
import java.io.IOException;

public class AuthorizeHelper {

    public static final String AUTHORIZE_FILE_DIR="users/";
    static {
        new File(AUTHORIZE_FILE_DIR).mkdirs();
    }

    public static boolean checkAuthorize(TunnelUser user) throws IOException {
        File file=new File(AUTHORIZE_FILE_DIR+user.getUsername()+".config");
        if(!file.exists())
            return false;

        byte[] buf= StreamUtils.readFile(file);
        String passwordHash=new String(buf);
        return passwordHash.equals(user.getPasswordHash());
    }

    public static TunnelUser getUser(String username) throws UserDoesNotExistException, IOException {
        File file=new File(AUTHORIZE_FILE_DIR+username+".config");
        if(!file.exists())
            throw new UserDoesNotExistException(username);

        byte[] buf= StreamUtils.readFile(file);
        String passwordHash=new String(buf);

        return new TunnelUser(username,passwordHash);
    }

}
