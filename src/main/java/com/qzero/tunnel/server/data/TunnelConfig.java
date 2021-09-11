package com.qzero.tunnel.server.data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class TunnelConfig {

    @Id
    private int tunnelPort;

    private String tunnelOwner;

    private String localIp;
    private int localPort;

    public TunnelConfig() {
    }

    public TunnelConfig(int tunnelPort, String tunnelOwner, String localIp, int localPort) {
        this.tunnelPort = tunnelPort;
        this.tunnelOwner = tunnelOwner;
        this.localIp = localIp;
        this.localPort = localPort;
    }

    public int getTunnelPort() {
        return tunnelPort;
    }

    public void setTunnelPort(int tunnelPort) {
        this.tunnelPort = tunnelPort;
    }

    public String getTunnelOwner() {
        return tunnelOwner;
    }

    public void setTunnelOwner(String tunnelOwner) {
        this.tunnelOwner = tunnelOwner;
    }

    public String getLocalIp() {
        return localIp;
    }

    public void setLocalIp(String localIp) {
        this.localIp = localIp;
    }

    public int getLocalPort() {
        return localPort;
    }

    public void setLocalPort(int localPort) {
        this.localPort = localPort;
    }

    @Override
    public String toString() {
        return "TunnelConfig{" +
                "tunnelPort=" + tunnelPort +
                ", tunnelOwner='" + tunnelOwner + '\'' +
                ", localIp='" + localIp + '\'' +
                ", localPort=" + localPort +
                '}';
    }
}
