package com.qzero.tunnel.test;

import com.qzero.tunnel.crypto.modules.TestModule;
import com.qzero.tunnel.relay.RelaySession;
import org.junit.Test;

import java.net.ServerSocket;
import java.net.Socket;

public class RelayTest {

    @Test
    public void testRelaySession() throws Exception{
        new TestServer().start();

        ServerSocket serverSocket=new ServerSocket(6666);
        Socket tunnelToServer=serverSocket.accept();

        Socket directToServer=new Socket("127.0.0.1",7777);

        RelaySession session=new RelaySession();
        session.setTunnelClient(tunnelToServer);
        session.setDirectClient(directToServer);

        session.initializeCryptoModule(new TestModule(),null);
        session.startRelay();

        Thread.sleep(1000000);
    }

}
