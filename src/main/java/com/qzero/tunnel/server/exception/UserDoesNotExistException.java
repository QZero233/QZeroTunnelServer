package com.qzero.tunnel.server.exception;

public class UserDoesNotExistException extends ResponsiveException {

    private String username;

    public UserDoesNotExistException(String username) {
        super(ErrorCodeList.CODE_WRONG_LOGIN_INFO,String.format("User named %s does not exists", username));
        this.username = username;
    }

}
