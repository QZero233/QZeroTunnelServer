package com.qzero.tunnel.server.data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class UserToken {

    @Id
    private String tokenId;
    private String username;

    public UserToken() {
    }

    public UserToken(String tokenId, String username) {
        this.tokenId = tokenId;
        this.username = username;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "UserToken{" +
                "tokenId='" + tokenId + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
