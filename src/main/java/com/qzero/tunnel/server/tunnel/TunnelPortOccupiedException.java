package com.qzero.tunnel.server.tunnel;

public class TunnelPortOccupiedException extends Exception{

    public TunnelPortOccupiedException(int port) {
        super(String.format("Tunnel port %d has been occupied", port));
    }
}
