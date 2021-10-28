package com.qzero.tunnel.server.data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class NATTraverseMapping {

    @Id
    private int tunnelPort;
    private String localIp;
    private int localPort;

    public NATTraverseMapping() {
    }

    public NATTraverseMapping(int tunnelPort, String localIp, int localPort) {
        this.tunnelPort = tunnelPort;
        this.localIp = localIp;
        this.localPort = localPort;
    }

    public int getTunnelPort() {
        return tunnelPort;
    }

    public void setTunnelPort(int tunnelPort) {
        this.tunnelPort = tunnelPort;
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
        return "NATTraverseMapping{" +
                "tunnelPort=" + tunnelPort +
                ", localIp='" + localIp + '\'' +
                ", localPort=" + localPort +
                '}';
    }
}
