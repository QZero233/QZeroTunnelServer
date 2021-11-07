package com.qzero.tunnel.server.proxy;

public class ProxyDstInfo {

    private String userToken;
    private String host;
    private int port;

    public ProxyDstInfo(String userToken, String host, int port) {
        this.userToken = userToken;
        this.host = host;
        this.port = port;
    }

    public ProxyDstInfo() {
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "ProxyHandshakeInfo{" +
                "userToken='" + userToken + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                '}';
    }
}
