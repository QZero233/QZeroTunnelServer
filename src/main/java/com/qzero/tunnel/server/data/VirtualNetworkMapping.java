package com.qzero.tunnel.server.data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class VirtualNetworkMapping {

    //Such as domain or ip address
    @Id
    private String dstIdentity;
    private String dstUserName;
    //FIXME there are permission crisis

    public VirtualNetworkMapping() {
    }

    public VirtualNetworkMapping(String dstIdentity, String dstUserName) {
        this.dstIdentity = dstIdentity;
        this.dstUserName = dstUserName;
    }

    public String getDstIdentity() {
        return dstIdentity;
    }

    public void setDstIdentity(String dstIdentity) {
        this.dstIdentity = dstIdentity;
    }

    public String getDstUserName() {
        return dstUserName;
    }

    public void setDstUserName(String dstUserName) {
        this.dstUserName = dstUserName;
    }

    @Override
    public String toString() {
        return "VirtualNetworkMapping{" +
                "dstIdentity='" + dstIdentity + '\'' +
                ", dstUserName='" + dstUserName + '\'' +
                '}';
    }
}
