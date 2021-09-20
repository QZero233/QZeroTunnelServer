package com.qzero.tunnel.server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.ArrayList;
import java.util.List;

@Configuration
@PropertySource("classpath:server.properties")
@ConfigurationProperties(prefix = "server")
public class ServerConfig {

    private int remindServerPort=9999;

    private int receptionServerPort=9998;

    private List<Integer> bannedPorts=new ArrayList<>();

    public ServerConfig() {
    }

    public ServerConfig(int remindServerPort, int receptionServerPort, List<Integer> bannedPorts) {
        this.remindServerPort = remindServerPort;
        this.receptionServerPort = receptionServerPort;
        this.bannedPorts = bannedPorts;
    }

    public int getRemindServerPort() {
        return remindServerPort;
    }

    public void setRemindServerPort(int remindServerPort) {
        this.remindServerPort = remindServerPort;
    }

    public int getReceptionServerPort() {
        return receptionServerPort;
    }

    public void setReceptionServerPort(int receptionServerPort) {
        this.receptionServerPort = receptionServerPort;
    }

    public List<Integer> getBannedPorts() {
        return bannedPorts;
    }

    public void setBannedPorts(List<Integer> bannedPorts) {
        this.bannedPorts = bannedPorts;
    }

    @Override
    public String toString() {
        return "ServerConfig{" +
                "remindServerPort=" + remindServerPort +
                ", receptionServerPort=" + receptionServerPort +
                ", bannedPorts=" + bannedPorts +
                '}';
    }
}
