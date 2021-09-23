package com.qzero.tunnel.server.exception;

public class TunnelDoesNotExistException extends ResponsiveException{

    public TunnelDoesNotExistException(int port){
        super(ErrorCodeList.CODE_MISSING_RESOURCE,String.format("Tunnel with port %d does not exist", port));
    }

}
