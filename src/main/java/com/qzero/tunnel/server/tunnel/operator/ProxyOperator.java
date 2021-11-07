package com.qzero.tunnel.server.tunnel.operator;

import com.qzero.tunnel.server.SpringUtil;
import com.qzero.tunnel.server.authorize.AuthorizeService;
import com.qzero.tunnel.server.crypto.CryptoModule;
import com.qzero.tunnel.server.crypto.CryptoModuleFactory;
import com.qzero.tunnel.server.crypto.modules.PlainModule;
import com.qzero.tunnel.server.data.TunnelConfig;
import com.qzero.tunnel.server.data.TunnelUser;
import com.qzero.tunnel.server.proxy.ProxyDstInfo;
import com.qzero.tunnel.server.relay.RelaySession;
import com.qzero.tunnel.server.tunnel.NewClientConnectedCallback;
import com.qzero.tunnel.server.tunnel.TunnelServerThread;
import com.qzero.tunnel.server.utils.StreamUtils;
import com.qzero.tunnel.server.utils.UUIDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ProxyOperator implements TunnelOperator{

    private Logger log= LoggerFactory.getLogger(getClass());

    private AuthorizeService authorizeService;

    private TunnelConfig tunnelConfig;

    private TunnelServerThread tunnelServerThread;
    private boolean isRunning=false;

    private Map<String,RelaySession> relaySessionMap=new HashMap<>();

    private NewClientConnectedCallback callback=clientSocket -> {
        //Send crypto module name to client
        try {
            String cryptoModuleName=tunnelConfig.getCryptoModuleName();

            OutputStream os=clientSocket.getOutputStream();
            StreamUtils.writeIntWith4Bytes(os,cryptoModuleName.getBytes(StandardCharsets.UTF_8).length);
            os.write(cryptoModuleName.getBytes(StandardCharsets.UTF_8));
        }catch (Exception e){
            log.error("Failed to send crypto module name to client",e);
            try {
                clientSocket.close();
            }catch (Exception e1){}
        }

        CryptoModule tunnelToServerModule=CryptoModuleFactory.getModule(tunnelConfig.getCryptoModuleName());
        try {
            tunnelToServerModule.doHandshakeAsServer(clientSocket.getInputStream(),clientSocket.getOutputStream());
        }catch (Exception e) {
            log.error(String.format("Failed to handshake with client %s, can not start relay session",
                    clientSocket.getInetAddress().getHostAddress()), e);

            try {
                clientSocket.close();
            } catch (Exception e1) {}

        }

        ProxyDstInfo dstInfo=null;
        try {
            dstInfo= getProxyDestination(clientSocket,tunnelToServerModule);

            TunnelUser tunnelUser=authorizeService.getUserByToken(dstInfo.getUserToken());
            if(tunnelUser==null || !tunnelUser.getUsername().equals(tunnelConfig.getTunnelOwner()))
                throw new Exception("Permission denied, no such a user or he has no access to this proxy tunnel");
        }catch (Exception e){
            log.trace("Failed to do handshake with proxy client "+clientSocket.getInetAddress().getHostName(), e);

            try {
                clientSocket.close();
            }catch (Exception e1){
                log.trace("Failed to close connection with proxy client "+clientSocket.getInetAddress().getHostName(), e1);
            }
        }

        startProxyRelaySession(dstInfo,clientSocket,tunnelToServerModule);
    };

    public ProxyOperator(TunnelConfig tunnelConfig) {
        this.tunnelConfig = tunnelConfig;
        authorizeService= SpringUtil.getBean(AuthorizeService.class);
    }

    @Override
    public void openTunnel() throws Exception {
        if(!CryptoModuleFactory.hasModule(tunnelConfig.getCryptoModuleName())){
            throw new Exception(String.format("Crypto module named %s does not exist", tunnelConfig.getCryptoModuleName()));
        }

        if(isRunning){
            throw new Exception("Tunnel is running, can not open it");
        }

        tunnelServerThread=new TunnelServerThread(tunnelConfig,callback);
        tunnelServerThread.startServerSocket();
        tunnelServerThread.start();
        isRunning=true;
    }

    @Override
    public void closeTunnel() throws Exception {
        if(!isRunning){
            throw new Exception("Tunnel is not running, can not close it");
        }

        tunnelServerThread.closeTunnel();

        Set<String> keySet=relaySessionMap.keySet();
        for(String key:keySet){
            relaySessionMap.get(key).closeSession();
        }
        isRunning=false;
    }

    @Override
    public boolean isTunnelRunning() {
        return isRunning;
    }

    private void startProxyRelaySession(ProxyDstInfo handshakeInfo, Socket clientSocket, CryptoModule tunnelToServerModule){
        RelaySession relaySession=new RelaySession();
        relaySession.setTunnelClient(clientSocket);

        try {
            Socket remote=new Socket(handshakeInfo.getHost(),handshakeInfo.getPort());
            relaySession.setDirectClient(remote);
        }catch (Exception e){
            log.error("Failed to connect to remote host, relay session closed");
            relaySession.closeSession();
        }

        String sessionId= UUIDUtils.getRandomUUID();
        relaySession.setCloseCallback(()->{
            relaySessionMap.remove(sessionId);
        });
        relaySessionMap.put(sessionId,relaySession);

        relaySession.initializeCryptoModule(tunnelToServerModule,new PlainModule());
        relaySession.startRelay();
    }

    /*
    4 bytes length of encrypted data
    n bytes encrypted data package

    within data package:
    4 bytes length of token
    n bytes token
    4 bytes length of host
    n bytes host
    4 bytes port
     */

    private ProxyDstInfo getProxyDestination(Socket clientSocket,CryptoModule cryptoModule) throws Exception{
        InputStream is=clientSocket.getInputStream();
        int lengthOfEncrypted=StreamUtils.readIntWith4Bytes(is);
        byte[] buf=StreamUtils.readSpecifiedLengthDataFromInputStream(is,lengthOfEncrypted);
        buf=cryptoModule.decrypt(buf);

        ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(buf);

        int lengthOfToken= StreamUtils.readIntWith4Bytes(byteArrayInputStream);
        String token=new String(StreamUtils.readSpecifiedLengthDataFromInputStream(byteArrayInputStream,lengthOfToken));

        int lengthOfHost=StreamUtils.readIntWith4Bytes(byteArrayInputStream);
        String host=new String(StreamUtils.readSpecifiedLengthDataFromInputStream(byteArrayInputStream,lengthOfHost));

        int port=StreamUtils.readIntWith4Bytes(byteArrayInputStream);

        return new ProxyDstInfo(token,host,port);
    }
}
