package cn.st2026.hermes.handlers;

import cn.st2026.hermes.cfg.SecureChatSslContextFactory;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLEngine;

/**
 * Created by Sloong on 2015/9/2.
 */
@Component
@Qualifier("httpChannelInitalizer")
public class HttpChannelInitalizer extends ChannelInitializer<SocketChannel> {

    @Autowired
    HttpResponseEncoder httpResponseEncoder;

    @Autowired
    HttpRequestDecoder httpRequestDecoder;

    @Autowired
    HttpServerInboundHandler httpServerInboundHandler;

    @Autowired
    HttpServerHandler httpServerHandler;

    @Autowired
    HttpHelloWorldServerHandler httpHelloWorldServerHandler;

    //@Autowired
    boolean isSSL = false;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception{
        ChannelPipeline pipeline = ch.pipeline();

        if (isSSL) {
            SSLEngine engine = SecureChatSslContextFactory.getServerContext().createSSLEngine();
            engine.setNeedClientAuth(true); //ssl双向认证
            engine.setUseClientMode(false);
            engine.setWantClientAuth(true);
            engine.setEnabledProtocols(new String[]{"SSLv3"});
            pipeline.addLast("ssl", new SslHandler(engine));
        }

        //pipeline.addLast("decoder",new HttpRequestDecoder());
        //pipeline.addLast("handler",httpServerInboundHandler);
        //pipeline.addLast("handler",httpServerHandler);
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast("handler",httpHelloWorldServerHandler);
        //pipeline.addLast("encoder",new HttpResponseEncoder());
    }

    public HttpServerHandler getHttpServerHandler() {
        return httpServerHandler;
    }

    public void setHttpServerHandler(HttpServerHandler httpServerHandler) {
        this.httpServerHandler = httpServerHandler;
    }

    public HttpResponseEncoder getHttpResponseEncoder() {
        return httpResponseEncoder;
    }

    public void setHttpResponseEncoder(HttpResponseEncoder httpResponseEncoder) {
        this.httpResponseEncoder = httpResponseEncoder;
    }

    public HttpRequestDecoder getHttpRequestDecoder() {
        return httpRequestDecoder;
    }

    public void setHttpRequestDecoder(HttpRequestDecoder httpRequestDecoder) {
        this.httpRequestDecoder = httpRequestDecoder;
    }

    public HttpServerInboundHandler getHttpServerInboundHandler() {
        return httpServerInboundHandler;
    }

    public void setHttpServerInboundHandler(HttpServerInboundHandler httpServerInboundHandler) {
        this.httpServerInboundHandler = httpServerInboundHandler;
    }

    public boolean isSSL() {
        return isSSL;
    }

    public void setIsSSL(boolean isSSL) {
        this.isSSL = isSSL;
    }
}
