package com.qzero.tunnel.test;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TestServer extends Thread{

    private ServerSocket serverSocket;

    public TestServer() throws Exception{
        serverSocket=new ServerSocket(7777);
    }

    @Override
    public void run() {
        super.run();

        try {
            while (true){
                Socket socket=serverSocket.accept();
                new Thread(){
                    @Override
                    public void run() {
                        super.run();

                        try {
                            InputStream is=socket.getInputStream();
                            OutputStream os=socket.getOutputStream();

                            while (true){
                                int i=is.read();
                                if(i==-1)
                                    break;

                                os.write(i);
                                System.out.println(i);
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                }.start();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
