package com.qzero.tunnel.crypto;

public class CryptoException extends Exception{

    private String moduleName;
    private String action;
    private DataWithLength data;

    private Throwable e;

    public CryptoException(String moduleName, String action, DataWithLength data, Throwable e) {
        this.moduleName = moduleName;
        this.action = action;
        this.data = data;
        this.e = e;
    }

    @Override
    public String getMessage() {
        return String.format("Error when module '%s' \n doing action '%s' \n with data %s \n detailed: %s",
                moduleName,action,data+"",e.getMessage());
    }
}
