package com.qzero.tunnel.server.exception;

public class TunnelPortOccupiedException extends ResponsiveException{

    public TunnelPortOccupiedException(int port) {
        super(ErrorCodeList.CODE_BAD_REQUEST_PARAMETER,String.format("Tunnel port %d has been occupied", port));
    }
}
