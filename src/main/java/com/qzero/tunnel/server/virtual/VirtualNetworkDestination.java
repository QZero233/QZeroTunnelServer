package com.qzero.tunnel.server.virtual;

public class VirtualNetworkDestination {

    private String username;
    private int port;

    public VirtualNetworkDestination() {
    }

    public VirtualNetworkDestination(String username, int port) {
        this.username = username;
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "VirtualNetworkDestination{" +
                "username='" + username + '\'' +
                ", port=" + port +
                '}';
    }
}
