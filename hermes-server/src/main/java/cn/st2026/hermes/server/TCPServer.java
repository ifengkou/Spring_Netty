package cn.st2026.hermes.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;

/**
 * Created by Sloong on 2015/9/2.
 */
//@Component
public class TCPServer {
    private static final Logger LOG = LoggerFactory.getLogger(TCPServer.class);

    @Autowired
    @Qualifier("tcpServerBootstrap")
    private ServerBootstrap b;

    @Autowired
    @Qualifier("tcpSocketAddress")
    private InetSocketAddress tcpPort;

    private Channel serverChannel;

    //@PostConstruct
    public void start() throws Exception{
        LOG.info("Starting tcp server at {}",tcpPort);
        //serverChannel = b.bind(tcpPort).sync().channel().closeFuture().sync().channel();
        serverChannel = b.bind(tcpPort).sync().channel();

        LOG.info("Waiting tcp server stopping......");
        serverChannel.closeFuture().sync();
    }

    //@PreDestroy
    public void stop(){
        LOG.info("Stopping tcp server...");
        serverChannel.close();
    }

    public ServerBootstrap getB() {
        return b;
    }

    public void setB(ServerBootstrap b) {
        this.b = b;
    }

    public InetSocketAddress getTcpPort() {
        return tcpPort;
    }

    public void setTcpPort(InetSocketAddress tcpPort) {
        this.tcpPort = tcpPort;
    }
}
