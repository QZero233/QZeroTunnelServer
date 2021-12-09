package com.qzero.tunnel.server.data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class TunnelConfig {

    @Id
    private int tunnelPort;

    private String tunnelOwner;

    private String cryptoModuleName;

    private int tunnelType;

    public static final int TYPE_NAT_TRAVERSE=1;
    public static final int TYPE_PROXY=2;
    public static final int TYPE_VIRTUAL_NETWORK=3;

    public TunnelConfig() {
    }

    public TunnelConfig(int tunnelPort, String tunnelOwner, String cryptoModuleName, int tunnelType) {
        this.tunnelPort = tunnelPort;
        this.tunnelOwner = tunnelOwner;
        this.cryptoModuleName = cryptoModuleName;
        this.tunnelType = tunnelType;
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

    public String getCryptoModuleName() {
        return cryptoModuleName;
    }

    public void setCryptoModuleName(String cryptoModuleName) {
        this.cryptoModuleName = cryptoModuleName;
    }

    public int getTunnelType() {
        return tunnelType;
    }

    public void setTunnelType(int tunnelType) {
        this.tunnelType = tunnelType;
    }

    @Override
    public String toString() {
        return "TunnelConfig{" +
                "tunnelPort=" + tunnelPort +
                ", tunnelOwner='" + tunnelOwner + '\'' +
                ", cryptoModuleName='" + cryptoModuleName + '\'' +
                ", tunnelType=" + tunnelType +
                '}';
    }
}
