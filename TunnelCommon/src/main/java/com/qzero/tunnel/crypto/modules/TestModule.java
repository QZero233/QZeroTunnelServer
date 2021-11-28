package com.qzero.tunnel.crypto.modules;


import com.qzero.tunnel.crypto.CryptoException;
import com.qzero.tunnel.crypto.CryptoModule;
import com.qzero.tunnel.crypto.DataWithLength;

import java.io.ByteArrayOutputStream;

public class TestModule implements CryptoModule {
    @Override
    public DataWithLength encrypt(DataWithLength data) throws CryptoException {
        /*
        读入和输入的序列是不一样的，可能
        客户端读入 1 2 3 4 5，加密后 2 2 3 4 5
        然后服务器读 2 2 3，再读 4 5，其中4 5又解密了一次，整段数据变为了 1 2 3 3 5
        要不尝试规定每次读取和写入的数据量？
        或者每个数据包前4位就是该包的数据量
         */

        byte[] buf=data.getData();
        byte tmp=buf[0];
        buf[0]=0;

        if(buf.length>data.getLength()){
            buf[data.getLength()]=tmp;
        }else{
            try {
                ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
                byteArrayOutputStream.write(buf);
                byteArrayOutputStream.write(tmp);

                data.setData(byteArrayOutputStream.toByteArray());
            }catch (Exception e){
                throw new CryptoException("test","encrypt",data,e);
            }
        }

        data.setLength(data.getLength()+1);

        return data;
    }

    @Override
    public DataWithLength decrypt(DataWithLength data) {
        byte[] buf=data.getData();
        buf[0]=buf[data.getLength()-1];
        data.setData(buf);
        data.setLength(data.getLength()-1);

        return data;
    }

}
