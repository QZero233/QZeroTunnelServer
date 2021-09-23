package com.qzero.tunnel.server.tunnel;

import com.qzero.tunnel.server.config.ServerConfig;
import com.qzero.tunnel.server.data.TunnelConfig;
import com.qzero.tunnel.server.data.repositories.TunnelConfigRepository;
import com.qzero.tunnel.server.exception.TunnelDoesNotExistException;
import com.qzero.tunnel.server.exception.TunnelPortOccupiedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TunnelService {

    private Map<Integer, TunnelOperator> tunnelMap =new HashMap<>();

    private List<Integer> bannedPorts=new ArrayList<>();

    private final TunnelConfigRepository configRepository;

    @Autowired
    public TunnelService(ServerConfig config, TunnelConfigRepository configRepository){
        if(config.getBannedPorts()!=null)
            bannedPorts=config.getBannedPorts();
        this.configRepository = configRepository;
        bannedPorts.add(config.getRemindServerPort());
        bannedPorts.add(config.getReceptionServerPort());
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
        if(configRepository.existsByTunnelPort(port) || bannedPorts.contains(port))
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

    public TunnelConfig getTunnelConfig(int tunnelPort){
        return configRepository.getByTunnelPort(tunnelPort);
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
