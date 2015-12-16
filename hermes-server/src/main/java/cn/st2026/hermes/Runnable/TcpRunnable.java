package cn.st2026.hermes.Runnable;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

/**
 * Created by Sloong on 2015/9/8.
 */
@Component
@Qualifier("tcpRunnable")
public class TcpRunnable implements Runnable{
    private static final Logger LOG = LoggerFactory.getLogger(TcpRunnable.class);
    private volatile boolean running =false;
    public TcpRunnable() {
        LOG.info("TcpRunnable init");
        running = false;
    }

    @Autowired
    @Qualifier("tcpServerBootstrap")
    private ServerBootstrap b;

    @Autowired
    @Qualifier("tcpSocketAddress")
    private InetSocketAddress tcpPort;

    private Channel serverChannel;

    @Override
    public void run(){
        LOG.info("Starting tcp server at {}", tcpPort);
        if(running==false) {
            running = true;
            try {
                //serverChannel = b.bind(tcpPort).sync().channel().closeFuture().sync().channel();
                serverChannel = b.bind(tcpPort).sync().channel();

                LOG.info("Waiting tcp server stopping......");
                serverChannel.closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
                LOG.error(e.getCause().getMessage());
            } finally {
                serverChannel.close();
            }
        }
    }

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
