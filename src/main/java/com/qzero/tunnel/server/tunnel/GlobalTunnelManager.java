package com.qzero.tunnel.server.tunnel;

import com.qzero.tunnel.server.SpringUtil;
import com.qzero.tunnel.server.config.GlobalConfigurationManager;
import com.qzero.tunnel.server.config.ServerConfiguration;
import com.qzero.tunnel.server.data.TunnelConfig;
import com.qzero.tunnel.server.data.repositories.TunnelConfigRepository;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlobalTunnelManager {

    private Map<Integer, TunnelOperator> tunnelMap =new HashMap<>();

    private static GlobalTunnelManager instance;

    private List<String> bannedPorts;

    private TunnelConfigRepository configRepository;

    public static GlobalTunnelManager getInstance(){
        if(instance==null)
            instance=new GlobalTunnelManager();
        return instance;
    }

    private GlobalTunnelManager(){
        ServerConfiguration configuration=GlobalConfigurationManager.getInstance().getServerConfiguration();
        bannedPorts=configuration.getBannedTunnelPorts();
        if(bannedPorts==null)
            bannedPorts=new ArrayList<>();
        bannedPorts.add(configuration.getReceptionServerPort()+"");
        bannedPorts.add(configuration.getCommandServerPort()+"");

        configRepository= SpringUtil.getBean(TunnelConfigRepository.class);
    }

    public void updateTunnel(TunnelConfig config) throws TunnelDoesNotExistException {
        int port=config.getTunnelPort();
        if(!configRepository.existsByTunnelPort(port))
            throw new TunnelDoesNotExistException(port);

        TunnelConfig origin=configRepository.getByTunnelPort(config.getTunnelPort());
        config.setTunnelOwner(origin.getTunnelOwner());

        configRepository.save(config);

        if(tunnelMap.containsKey(config.getTunnelPort()))
            tunnelMap.get(config.getTunnelPort()).updateTunnelConfig(config);
    }

    public void newTunnel(TunnelConfig config) throws TunnelPortOccupiedException {
        int port=config.getTunnelPort();
        if(configRepository.existsByTunnelPort(port) || bannedPorts.contains(port+""))
            throw new TunnelPortOccupiedException(port);

        configRepository.save(config);
    }

    public void openTunnel(int port) throws IOException, TunnelDoesNotExistException {
        if(!configRepository.existsByTunnelPort(port))
            throw new TunnelDoesNotExistException(port);

        TunnelOperator operator;
        if(!tunnelMap.containsKey(port)){
            TunnelConfig config=configRepository.getByTunnelPort(port);
            operator=new TunnelOperator(config);
            tunnelMap.put(port,operator);
        }else{
            operator=tunnelMap.get(port);
        }

        if(operator.isTunnelRunning()){
            return;
        }
        operator.openTunnel();;
    }

    public void closeTunnel(int port) throws IOException, TunnelDoesNotExistException {
        if(!tunnelMap.containsKey(port))
            return;

        tunnelMap.get(port).closeTunnel();
    }

    public TunnelOperator getTunnelOperator(int port){
        return tunnelMap.get(port);
    }

    public boolean hasTunnel(int port){
        return configRepository.existsByTunnelPort(port);
    }

    public void closeAllTunnelByOwner(String owner) throws IOException {
        List<TunnelConfig> configList=configRepository.findAllByTunnelOwner(owner);
        if(configList==null)
            return;

        for(TunnelConfig config:configList){
            if(!tunnelMap.containsKey(config.getTunnelPort()))
                continue;

            TunnelOperator operator=tunnelMap.get(config.getTunnelPort());
            operator.closeTunnel();
        }
    }

}
