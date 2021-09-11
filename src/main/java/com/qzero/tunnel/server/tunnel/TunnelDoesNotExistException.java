package com.qzero.tunnel.server.tunnel;

public class TunnelDoesNotExistException extends Exception{

    public TunnelDoesNotExistException(int port){
        super(String.format("Tunnel with port %d does not exist", port));
    }

}
