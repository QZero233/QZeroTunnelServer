package com.qzero.tunnel.server.tunnel.operator;

import com.qzero.tunnel.crypto.CryptoModule;
import com.qzero.tunnel.crypto.CryptoModuleFactory;
import com.qzero.tunnel.crypto.DataWithLength;
import com.qzero.tunnel.relay.RelaySession;
import com.qzero.tunnel.relay.RelayStrategy;
import com.qzero.tunnel.server.SpringUtil;
import com.qzero.tunnel.server.data.NATTraverseMapping;
import com.qzero.tunnel.server.data.TunnelConfig;
import com.qzero.tunnel.server.relay.remind.RemindClientContainer;
import com.qzero.tunnel.server.relay.remind.RemindClientProcessThread;
import com.qzero.tunnel.server.virtual.VirtualNetworkDestination;
import com.qzero.tunnel.server.virtual.VirtualNetworkMappingService;
import com.qzero.tunnel.utils.StreamUtils;
import com.qzero.tunnel.utils.UUIDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

//FIXME check user's identity
public class VirtualNetworkOperator extends BaseTunnelOperator implements TunnelOperator {

    private Logger log= LoggerFactory.getLogger(getClass());

    private VirtualNetworkMappingService service= SpringUtil.getBean(VirtualNetworkMappingService.class);

    public VirtualNetworkOperator(TunnelConfig config) {
        super(config);
    }

    public void startRelaySession(String sessionId, Socket tunnelSocket){
        RelaySession session=sessionMap.get(sessionId);

        if(session==null)
            throw new IllegalArgumentException(String.format("Relay session with id %s does not exist", sessionId));

        session.setTunnelClient(tunnelSocket);

        //Initialize crypto module
        CryptoModule tunnelToServerModule= CryptoModuleFactory.getModule(config.getCryptoModuleName());
        try {
            tunnelToServerModule.doHandshakeAsServer(tunnelSocket.getInputStream(),tunnelSocket.getOutputStream());
        }catch (Exception e){
            log.error(String.format("Failed to handshake with client %s, can not start relay session",
                    tunnelSocket.getInetAddress().getHostAddress()),e);
            session.closeSession();
        }
        session.setTunnelToServerModule(tunnelToServerModule);

        //Start relay
        session.startRelay();
    }

    @Override
    protected void onNewClientConnected(Socket clientSocket) {
        //First, send crypto module and do crypto module handshake
        CryptoModule directToServerModule= CryptoModuleFactory.getModule(config.getCryptoModuleName());
        try {

            String cryptoModuleName=config.getCryptoModuleName();
            StreamUtils.writeIntWith4Bytes(clientSocket.getOutputStream(),cryptoModuleName.getBytes(StandardCharsets.UTF_8).length);
            clientSocket.getOutputStream().write(cryptoModuleName.getBytes(StandardCharsets.UTF_8));

            directToServerModule.doHandshakeAsServer(clientSocket.getInputStream(),clientSocket.getOutputStream());
        }catch (Exception e){
            log.error(String.format("Failed to do crypto module handshake with direct client %s, can not start relay session",
                    clientSocket.getInetAddress().getHostAddress()),e);

            try {
                clientSocket.close();
            }catch (Exception ex){}

            //Crypto module handshake with direct client failed, we don't have to remind the other user
            return;
        }

        //Then get destination
        VirtualNetworkDestination destination;
        try {
            destination=doHandshake(clientSocket,directToServerModule);
        } catch (Exception e) {
            log.error("Failed to do handshake with virtual network client "+clientSocket.getInetAddress().getHostAddress(),e);

            //Disconnect
            try {
                clientSocket.close();
            } catch (IOException ex) {}

            return;
        }

        RemindClientContainer remindClientContainer=RemindClientContainer.getInstance();

        //Destination user is not online, disconnect
        if(!remindClientContainer.hasOnlineClient(destination.getUsername())){
            log.trace("Virtual network client connection is closed due to dst client being offline");

            //Disconnect
            try {
                clientSocket.close();
            } catch (IOException ex) {}

            return;
        }

        //Initialize relay session
        String sessionId= UUIDUtils.getRandomUUID();
        RelaySession relaySession=new RelaySession();

        //Modify default strategy
        //Since the direct client is also a tunnel client
        relaySession.setTunnelToDirectStrategy(new RelayStrategy(false,false));
        relaySession.setDirectToTunnelStrategy(new RelayStrategy(false,false));

        relaySession.setDirectClient(clientSocket);
        relaySession.setCloseCallback(() -> {
            sessionMap.remove(sessionId);
        });

        sessionMap.put(sessionId,relaySession);

        //Deploy crypto module
        relaySession.setDirectToServerModule(directToServerModule);

        //Remind dst client to join session
        RemindClientProcessThread processThread=remindClientContainer.getClient(destination.getUsername());
        processThread.remindRelayConnect(config,
                new NATTraverseMapping(config.getTunnelPort(),"127.0.0.1",destination.getPort()),
                sessionId);
    }

    /**
     * 4 bytes length of username
     * n bytes username
     * 4 bytes port
     */
    private VirtualNetworkDestination doHandshake(Socket clientSocket,CryptoModule cryptoModule) throws Exception{
        InputStream is=clientSocket.getInputStream();

        //First, read the length of encrypted data, then decrypt it
        int length=StreamUtils.readIntWith4Bytes(is);
        byte[] buf=StreamUtils.readSpecifiedLengthDataFromInputStream(is,length);
        DataWithLength data=cryptoModule.decrypt(new DataWithLength(buf,length));
        buf=data.getData();

        //Construct input stream to read
        ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(buf);

        int dstIdentityLength= StreamUtils.readIntWith4Bytes(byteArrayInputStream);
        byte[] dstIdentityBuf=StreamUtils.readSpecifiedLengthDataFromInputStream(byteArrayInputStream,dstIdentityLength);
        String dstIdentity=new String(dstIdentityBuf);

        String username=service.findDstUser(dstIdentity);

        int port=StreamUtils.readIntWith4Bytes(byteArrayInputStream);

        return new VirtualNetworkDestination(username,port);
    }

}
