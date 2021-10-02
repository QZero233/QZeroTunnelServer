package com.qzero.tunnel.server.config;

public class ServerPortInfo {

    private int remindServerPort;
    private int relaySeverPort;

    public ServerPortInfo() {
    }

    public ServerPortInfo(int remindServerPort, int relaySeverPort) {
        this.remindServerPort = remindServerPort;
        this.relaySeverPort = relaySeverPort;
    }

    public int getRemindServerPort() {
        return remindServerPort;
    }

    public void setRemindServerPort(int remindServerPort) {
        this.remindServerPort = remindServerPort;
    }

    public int getRelaySeverPort() {
        return relaySeverPort;
    }

    public void setRelaySeverPort(int relaySeverPort) {
        this.relaySeverPort = relaySeverPort;
    }

    @Override
    public String toString() {
        return "ServerPortInfo{" +
                "remindServerPort=" + remindServerPort +
                ", relaySeverPort=" + relaySeverPort +
                '}';
    }
}
