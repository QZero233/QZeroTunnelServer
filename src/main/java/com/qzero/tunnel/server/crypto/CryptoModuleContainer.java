package com.qzero.tunnel.server.crypto;

import com.qzero.tunnel.server.crypto.modules.PlainModule;

import java.util.HashMap;
import java.util.Map;

public class CryptoModuleContainer {

    private Map<String,CryptoModule> moduleMap=new HashMap<>();

    private static CryptoModuleContainer instance;

    public static CryptoModuleContainer getInstance(){
        if(instance==null)
            instance=new CryptoModuleContainer();
        return instance;
    }

    private CryptoModuleContainer(){

    }

    public void loadDefaultModules() throws Exception{
        registerModuleClass(PlainModule.class);
    }

    public void registerModuleClass(Class cls) throws Exception{
        CryptoModuleClass moduleAnnotation= (CryptoModuleClass) cls.getDeclaredAnnotation(CryptoModuleClass.class);
        if(moduleAnnotation==null){
            throw new Exception("Can not find CryptoModuleClass annotation in class "+cls.getName());
        }

        Class[] interfaces=cls.getInterfaces();
        boolean isModule=false;
        for(Class i:interfaces){
            if(i.equals(CryptoModule.class)){
                isModule=true;
                break;
            }
        }

        if(!isModule){
            throw new Exception(String.format("Class %s is not an implement of CryptoModule", cls.getName()));
        }

        CryptoModule module= (CryptoModule) cls.getConstructor().newInstance();
        addModule(moduleAnnotation.name(),module);
    }

    public void addModule(String name,CryptoModule module) throws Exception {
        if(moduleMap.containsKey(name)){
            throw new Exception(String.format("Module named %s has already been added", name));
        }
        moduleMap.put(name,module);
    }

    public void removeModule(String name){
        moduleMap.remove(name);
    }

    public CryptoModule getModule(String name){
        return moduleMap.get(name);
    }

    public boolean hasModule(String name){
        return moduleMap.containsKey(name);
    }

}
