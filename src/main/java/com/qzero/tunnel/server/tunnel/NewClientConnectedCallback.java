package com.qzero.tunnel.server.tunnel;

import java.net.Socket;

public interface NewClientConnectedCallback {

    void onConnected(Socket clientSocket);

}
