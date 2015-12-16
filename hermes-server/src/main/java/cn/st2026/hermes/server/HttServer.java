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
public class HttServer {
    private static final Logger LOG = LoggerFactory.getLogger(HttServer.class);

    @Autowired
    @Qualifier("httpServerBootstrap")
    private ServerBootstrap b;

    @Autowired
    @Qualifier("httpSocketAddress")
    private InetSocketAddress httpPort;

    private Channel serverChannel;

    //@PostConstruct
    public void start() throws Exception{
        LOG.info("Starting http server at {}", httpPort);
        //serverChannel = b.bind(httpPort).sync().channel().closeFuture().sync().channel();
        serverChannel = b.bind(httpPort).channel();//.closeFuture().sync().channel();

        LOG.info("Waiting http server stopping......");
        //serverChannel.closeFuture().sync();
    }

    //@PreDestroy
    public void stop(){
        LOG.info("Stopping http server...");
        serverChannel.close();
    }

    public ServerBootstrap getB() {
        return b;
    }

    public void setB(ServerBootstrap b) {
        this.b = b;
    }

    public InetSocketAddress getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(InetSocketAddress httpPort) {
        this.httpPort = httpPort;
    }
}
