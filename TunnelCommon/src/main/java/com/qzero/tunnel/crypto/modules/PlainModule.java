package com.qzero.tunnel.crypto.modules;

import com.qzero.tunnel.crypto.CryptoModule;
import com.qzero.tunnel.crypto.DataWithLength;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PlainModule implements CryptoModule {

    private int unitPackageLength=0;

    public PlainModule() {
    }

    public PlainModule(int unitPackageLength) {
        this.unitPackageLength = unitPackageLength;
    }

    public void setUnitPackageLength(int unitPackageLength) {
        this.unitPackageLength = unitPackageLength;
    }

    @Override
    public DataWithLength encrypt(DataWithLength data) {
        return data;
    }

    @Override
    public DataWithLength decrypt(DataWithLength data) {
        return data;
    }

    @Override
    public int getUnitPackageLength() {
        return unitPackageLength;
    }

    @Override
    public void doHandshakeAsClient(InputStream inputStream, OutputStream outputStream) throws IOException {
        CryptoModule.super.doHandshakeAsClient(inputStream, outputStream);
    }

    @Override
    public void doHandshakeAsServer(InputStream inputStream, OutputStream outputStream) throws IOException {
        CryptoModule.super.doHandshakeAsServer(inputStream, outputStream);
    }
}
