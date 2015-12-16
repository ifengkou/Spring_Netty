package cn.st2026.hermes.handlers;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


/**
 * Created by Sloong on 2015/9/2.
 */
@Component
@Qualifier("tcpChannelInitalizer")
public class TCPChannelInitalizer extends ChannelInitializer<SocketChannel> {

    @Autowired
    StringDecoder stringDecoder;

    @Autowired
    StringEncoder stringEncoder;

    @Autowired
    TcpServerHandler tcpServerHandler;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception{
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("decoder",stringDecoder);
        pipeline.addLast("handler", tcpServerHandler);
        pipeline.addLast("encoder",stringEncoder);
    }

    public StringDecoder getStringDecoder() {
        return stringDecoder;
    }

    public void setStringDecoder(StringDecoder stringDecoder) {
        this.stringDecoder = stringDecoder;
    }

    public StringEncoder getStringEncoder() {
        return stringEncoder;
    }

    public void setStringEncoder(StringEncoder stringEncoder) {
        this.stringEncoder = stringEncoder;
    }

    public TcpServerHandler getTcpServerHandler() {
        return tcpServerHandler;
    }

    public void setTcpServerHandler(TcpServerHandler tcpServerHandler) {
        this.tcpServerHandler = tcpServerHandler;
    }
}
