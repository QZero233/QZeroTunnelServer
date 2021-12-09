package com.qzero.tunnel.server.tunnel.operator;

import com.qzero.tunnel.server.data.TunnelConfig;

public interface TunnelOperator {

    void openTunnel() throws Exception;
    void closeTunnel() throws Exception;
    boolean isTunnelRunning();

    void updateTunnelConfig(TunnelConfig newConfig);

}
