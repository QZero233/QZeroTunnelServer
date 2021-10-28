package com.qzero.tunnel.server.tunnel.operator;

public interface TunnelOperator {

    void openTunnel() throws Exception;
    void closeTunnel() throws Exception;

    boolean isTunnelRunning();

}
