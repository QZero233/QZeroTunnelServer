package com.qzero.tunnel.server.authorize;

public class UserDoesNotExistException extends Exception {

    private String username;

    public UserDoesNotExistException(String username) {
        this.username = username;
    }

    @Override
    public String getMessage() {
        return String.format("User named %s does not exists", username);
    }
}
