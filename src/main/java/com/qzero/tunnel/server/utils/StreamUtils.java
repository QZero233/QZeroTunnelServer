package com.qzero.tunnel.server.utils;

import java.io.*;
import java.nio.ByteBuffer;

/**
 * Stream utils class
 */
public class StreamUtils {

    /**
     * Read data from input until there is no data left
     */
    public static byte[] readDataFromInputStream(InputStream inputStream) throws IOException {
        byte[] buf=new byte[1024];
        int len;
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        while((len=inputStream.read(buf))!=-1){
            outputStream.write(buf,0,len);
        }

        return outputStream.toByteArray();
    }


    public static void writeDataToOutputStream(OutputStream outputStream,byte[] data) throws IOException{
        outputStream.write(data);
    }

    public static byte[] readFile(File file) throws IOException {
        InputStream inputStream=new FileInputStream(file);
        byte[] result= readDataFromInputStream(inputStream);
        inputStream.close();
        return result;
    }

    public static void writeFile(File file,byte[] data) throws IOException{
        OutputStream outputStream=new FileOutputStream(file);
        writeDataToOutputStream(outputStream,data);
        outputStream.close();
    }

    /**
     * Make a 8-byte array to long
     * @throws IllegalArgumentException Thrown if the array length is not 8
     */
    public static long byteArrayToLong(byte[] buf) {
        if(buf.length!=8)
            throw new IllegalArgumentException("错误，不能将一个长度不为8的数组转为long");
        ByteBuffer buffer = ByteBuffer.wrap(buf);
        return buffer.getLong();
    }

    /**
     * Make a long to 8-byte array
     */
    public static byte[] longToByteArray(long l) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(l);
        return buffer.array();
    }

    public static int byteArrayToInt(byte[] buf) {
        if(buf.length!=4)
            throw new IllegalArgumentException("错误，不能将一个长度不为4的数组转为int");
        ByteBuffer buffer = ByteBuffer.wrap(buf);
        return buffer.getInt();
    }

    public static byte[] intToByteArray(int i) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(i);
        return buffer.array();
    }

    public static byte[] readSpecifiedLengthDataFromInputStream(InputStream is, int length) throws IOException {
        if(length==0)
            return new byte[0];

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(length);
        byte[] buf = new byte[length];
        int len;
        while (true) {
            len = is.read(buf, 0, length);
            length -= len;
            outputStream.write(buf, 0, len);
            if (length == 0)
                break;
        }

        return outputStream.toByteArray();

    }

    public static int readIntWith4Bytes(InputStream is) throws IOException{
        byte[] buf=readSpecifiedLengthDataFromInputStream(is,4);
        return byteArrayToInt(buf);
    }

    public static long readLongWith8Bytes(InputStream is) throws IOException{
        byte[] buf=readSpecifiedLengthDataFromInputStream(is,8);
        return byteArrayToLong(buf);
    }

    public static void writeIntWith4Bytes(OutputStream os,int i) throws IOException{
        byte[] buf=intToByteArray(i);
        os.write(buf);
    }

    public static void writeLongWith8Bytes(OutputStream os,long l) throws IOException{
        byte[] buf=longToByteArray(l);
        os.write(buf);
    }
}
