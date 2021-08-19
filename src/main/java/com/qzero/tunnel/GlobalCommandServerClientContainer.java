package com.qzero.tunnel;

import com.qzero.tunnel.command.CommandServerProcessThread;

import java.util.Map;

public class GlobalCommandServerClientContainer {

    private static GlobalCommandServerClientContainer instance;

    private Map<String, CommandServerProcessThread>

    private GlobalCommandServerClientContainer(){

    }

    public static GlobalCommandServerClientContainer getInstance() {
        if(instance==null)
            instance=new GlobalCommandServerClientContainer();
        return instance;
    }
}
