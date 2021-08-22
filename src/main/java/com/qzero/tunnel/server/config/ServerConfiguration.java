package com.qzero.tunnel.server.config;

import com.qzero.tunnel.server.config.utils.ConfigValueConvert;
import com.qzero.tunnel.server.config.utils.IntConverter;
import com.qzero.tunnel.server.config.utils.SingleLineListConverter;

import java.util.List;

public class ServerConfiguration {

    public static final int DEFAULT_COMMAND_SERVER_PORT=9999;
    public static final int DEFAULT_RECEPTION_SERVER_PORT=9998;

    @ConfigValueConvert(converter = IntConverter.class)
    private int commandServerPort;
    @ConfigValueConvert(converter = IntConverter.class)
    private int receptionServerPort;
    @ConfigValueConvert(converter = SingleLineListConverter.class)
    private List<String> bannedTunnelPorts;

    public int getCommandServerPort() {
        return commandServerPort;
    }

    public void setCommandServerPort(int commandServerPort) {
        this.commandServerPort = commandServerPort;
    }

    public int getReceptionServerPort() {
        return receptionServerPort;
    }

    public void setReceptionServerPort(int receptionServerPort) {
        this.receptionServerPort = receptionServerPort;
    }

    public List<String> getBannedTunnelPorts() {
        return bannedTunnelPorts;
    }

    public void setBannedTunnelPorts(List<String> bannedTunnelPorts) {
        this.bannedTunnelPorts = bannedTunnelPorts;
    }

    @Override
    public String toString() {
        return "ServerConfiguration{" +
                "commandServerPort=" + commandServerPort +
                ", receptionServerPort=" + receptionServerPort +
                ", bannedTunnelPorts=" + bannedTunnelPorts +
                '}';
    }
}
