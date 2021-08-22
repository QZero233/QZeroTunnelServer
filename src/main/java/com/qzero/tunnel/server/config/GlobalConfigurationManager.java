package com.qzero.tunnel.server.config;

import com.qzero.tunnel.server.config.utils.ConfigurationUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class GlobalConfigurationManager {

    public static final String SERVER_CONFIG_FILE="serverConfig.config";

    private static GlobalConfigurationManager instance;

    private ServerConfiguration serverConfiguration;

    private GlobalConfigurationManager(){

    }

    public static GlobalConfigurationManager getInstance() {
        if(instance==null)
            instance=new GlobalConfigurationManager();
        return instance;
    }

    public ServerConfiguration getServerConfiguration() {
        return serverConfiguration;
    }

    public void loadServerConfig() throws IOException, InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        File file=new File(SERVER_CONFIG_FILE);
        if(!file.exists())
            throw new FileNotFoundException(String.format("Server config file %s does not exist", SERVER_CONFIG_FILE));

        Map<String,String> config=ConfigurationUtils.readConfiguration(file);
        if(config==null)
            throw new IOException("Failed to read server config file");

        serverConfiguration= ConfigurationUtils.configToJavaBeanWithOnlyStringFields(config,ServerConfiguration.class);

        if(serverConfiguration.getCommandServerPort()<=0)
            serverConfiguration.setCommandServerPort(ServerConfiguration.DEFAULT_COMMAND_SERVER_PORT);

        if(serverConfiguration.getReceptionServerPort()<=0)
            serverConfiguration.setReceptionServerPort(ServerConfiguration.DEFAULT_RECEPTION_SERVER_PORT);
    }

}
