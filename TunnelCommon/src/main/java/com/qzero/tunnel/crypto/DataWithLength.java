package com.qzero.tunnel.crypto;

import java.util.Arrays;

public class DataWithLength {

    private byte[] data;
    private int length;

    public DataWithLength() {
    }

    public DataWithLength(byte[] data, int length) {
        this.data = data;
        this.length = length;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    @Override
    public String toString() {
        return "DataWithLength{" +
                "data=" + Arrays.toString(data) +
                ", length=" + length +
                '}';
    }
}
