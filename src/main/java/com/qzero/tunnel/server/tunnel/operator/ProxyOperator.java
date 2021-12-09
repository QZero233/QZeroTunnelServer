package com.qzero.tunnel.server.tunnel.operator;

import com.qzero.tunnel.crypto.CryptoModule;
import com.qzero.tunnel.crypto.CryptoModuleFactory;
import com.qzero.tunnel.crypto.DataWithLength;
import com.qzero.tunnel.relay.RelaySession;
import com.qzero.tunnel.server.SpringUtil;
import com.qzero.tunnel.server.authorize.AuthorizeService;
import com.qzero.tunnel.server.data.TunnelConfig;
import com.qzero.tunnel.server.data.TunnelUser;
import com.qzero.tunnel.server.proxy.ProxyDstInfo;
import com.qzero.tunnel.utils.StreamUtils;
import com.qzero.tunnel.utils.UUIDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ProxyOperator extends BaseTunnelOperator implements TunnelOperator{

    private Logger log= LoggerFactory.getLogger(getClass());

    private AuthorizeService authorizeService;

    public ProxyOperator(TunnelConfig config) {
        super(config);
        authorizeService= SpringUtil.getBean(AuthorizeService.class);
    }

    @Override
    protected void onNewClientConnected(Socket clientSocket) {
        //Send crypto module name to client
        try {
            String cryptoModuleName= config.getCryptoModuleName();

            OutputStream os=clientSocket.getOutputStream();
            StreamUtils.writeIntWith4Bytes(os,cryptoModuleName.getBytes(StandardCharsets.UTF_8).length);
            os.write(cryptoModuleName.getBytes(StandardCharsets.UTF_8));
        }catch (Exception e){
            log.error("Failed to send crypto module name to client",e);
            try {
                clientSocket.close();
            }catch (Exception e1){}
        }

        CryptoModule tunnelToServerModule= CryptoModuleFactory.getModule(config.getCryptoModuleName());
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

            log.trace(dstInfo+"");

            TunnelUser tunnelUser=authorizeService.getUserByToken(dstInfo.getUserToken());
            if(tunnelUser==null || !tunnelUser.getUsername().equals(config.getTunnelOwner()))
                throw new Exception("Permission denied, no such a user or he has no access to this proxy tunnel");
        }catch (Exception e){
            log.error("Failed to do handshake with proxy client "+clientSocket.getInetAddress().getHostName(), e);

            try {
                clientSocket.close();
            }catch (Exception e1){
                log.trace("Failed to close connection with proxy client "+clientSocket.getInetAddress().getHostName(), e1);
            }
        }

        startProxyRelaySession(dstInfo,clientSocket,tunnelToServerModule);
    }

    private void startProxyRelaySession(ProxyDstInfo handshakeInfo, Socket clientSocket, CryptoModule tunnelToServerModule){
        RelaySession relaySession=new RelaySession();
        relaySession.setTunnelClient(clientSocket);

        try {
            Socket remote=new Socket(handshakeInfo.getHost(),handshakeInfo.getPort());
            relaySession.setDirectClient(remote);
        }catch (Exception e){
            log.trace("Failed to connect to remote host, relay session closed");
            relaySession.closeSession();
            return;
        }

        String sessionId= UUIDUtils.getRandomUUID();
        relaySession.setCloseCallback(()->{
            sessionMap.remove(sessionId);
        });
        sessionMap.put(sessionId,relaySession);

        relaySession.initializeCryptoModule(tunnelToServerModule,null);
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
    private ProxyDstInfo getProxyDestination(Socket clientSocket, CryptoModule cryptoModule) throws Exception{
        InputStream is=clientSocket.getInputStream();
        int lengthOfEncrypted=StreamUtils.readIntWith4Bytes(is);
        byte[] buf=StreamUtils.readSpecifiedLengthDataFromInputStream(is,lengthOfEncrypted);

        DataWithLength data=new DataWithLength(buf,lengthOfEncrypted);
        data=cryptoModule.decrypt(data);

        if(data==null){
            //Normally it won't return null, it will throw exception
            throw new Exception("Crypto error without throwing exception");
        }

        buf=data.getData();

        ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(buf);

        int lengthOfToken= StreamUtils.readIntWith4Bytes(byteArrayInputStream);
        String token=new String(StreamUtils.readSpecifiedLengthDataFromInputStream(byteArrayInputStream,lengthOfToken));

        int lengthOfHost=StreamUtils.readIntWith4Bytes(byteArrayInputStream);
        String host=new String(StreamUtils.readSpecifiedLengthDataFromInputStream(byteArrayInputStream,lengthOfHost));

        int port=StreamUtils.readIntWith4Bytes(byteArrayInputStream);

        return new ProxyDstInfo(token,host,port);
    }
}
