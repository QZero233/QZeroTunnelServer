package com.qzero.tunnel.server.tunnel.operator;

import com.qzero.tunnel.server.data.TunnelConfig;

public class TunnelOperatorFactory {

    public static TunnelOperator getOperator(TunnelConfig config){
        if(config==null)
            return null;
        switch (config.getTunnelType()){
            case TunnelConfig.TYPE_NAT_TRAVERSE:
                return new NATTraverseTunnelOperator(config);
            case TunnelConfig.TYPE_PROXY:
                return new ProxyOperator(config);
            default:
                return null;
        }
    }

}
