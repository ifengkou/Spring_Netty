package cn.st2026.hermes.cfg;

import cn.st2026.hermes.handlers.HttpChannelInitalizer;
import cn.st2026.hermes.handlers.TCPChannelInitalizer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Sloong on 2015/9/2.
 */
@Configuration
@ComponentScan("cn.st2026.hermes")
@PropertySource("classpath:netty-server.properties")
public class SpringConfig {

    @Value("${boss.thread.count}")
    private int bossCount;

    @Value("${worker.thread.count}")
    private int workerCount;

    @Value("${tcp.port}")
    private int tcpPort;

    @Value("${so.keepalive}")
    private boolean keepAlive;

    @Value("${so.backlog}")
    private int backlog;

    @Value("${http.port}")
    private int httpPort;

    @Value("${http.isSSL}")
    private boolean isSSL;

    @Autowired
    @Qualifier("tcpChannelInitalizer")
    private TCPChannelInitalizer tcpChannelInitalizer;

    @Autowired
    @Qualifier("httpChannelInitalizer")
    private HttpChannelInitalizer httpChannelInitalizer;

    @SuppressWarnings("unchecked")
    @Bean(name = "tcpServerBootstrap")
    public ServerBootstrap tcpServerBootstrap(){
        ServerBootstrap b= new ServerBootstrap();
        b.group(bossGroup(),workerGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(tcpChannelInitalizer);
        Map<ChannelOption<?>,Object> tcpChannelOptions = tcpChannelOptions();
        Set<ChannelOption<?>> keySet = tcpChannelOptions.keySet();
        for (@SuppressWarnings("rawtypes") ChannelOption option : keySet) {
            b.option(option, tcpChannelOptions.get(option));
        }
        return b;
    }

    @SuppressWarnings("unchecked")
    @Bean(name="httpServerBootstrap")
    public ServerBootstrap httpServerBootstrap(){
        ServerBootstrap b= new ServerBootstrap();
        b.group(httpBossGroup(),httpWorkerGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(httpChannelInitalizer);
        Map<ChannelOption<?>,Object> httpChannelOptions = httpChannelOptions();
        Set<ChannelOption<?>> keySet = httpChannelOptions.keySet();
        for (@SuppressWarnings("rawtypes") ChannelOption option : keySet) {
            b.option(option, httpChannelOptions.get(option));
        }
        return b;
    }

    @Bean(name = "bossGroup",destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup bossGroup(){
        return new NioEventLoopGroup(bossCount);
    }

    @Bean(name = "workerGroup",destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup workerGroup(){
        return new NioEventLoopGroup(workerCount);
    }

    @Bean(name = "httpBossGroup",destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup httpBossGroup(){
        return new NioEventLoopGroup();
    }
    @Bean(name = "httpWorkerGroup",destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup httpWorkerGroup(){
        return new NioEventLoopGroup();
    }

    @Bean(name = "tcpSocketAddress")
    public InetSocketAddress tcpPort(){
        return new InetSocketAddress(tcpPort);
    }

    @Bean(name = "tcpChannelOptions")
    public Map<ChannelOption<?> ,Object> tcpChannelOptions(){
        Map<ChannelOption<?> ,Object> options = new HashMap<ChannelOption<?> ,Object>();
        options.put(ChannelOption.SO_KEEPALIVE, keepAlive);
        options.put(ChannelOption.SO_BACKLOG, backlog);
        return options;
    }

    @Bean(name = "httpSocketAddress")
    public InetSocketAddress httpPort(){
        return new InetSocketAddress(httpPort);
    }

    @Bean(name = "httpChannelOptions")
    public Map<ChannelOption<?> ,Object> httpChannelOptions(){
        Map<ChannelOption<?> ,Object> options = new HashMap<ChannelOption<?> ,Object>();
        options.put(ChannelOption.SO_KEEPALIVE, keepAlive);
        options.put(ChannelOption.SO_BACKLOG, backlog);
        return options;
    }

    public boolean isSSL() {
        return isSSL;
    }

    public void setIsSSL(boolean isSSL) {
        this.isSSL = isSSL;
    }

    @Bean(name = "stringEncoder")
    public StringEncoder stringEncoder() {
        return new StringEncoder();
    }

    @Bean(name = "stringDecoder")
    public StringDecoder stringDecoder() {
        return new StringDecoder();
    }

    @Bean(name = "httpResponseEncoder")
    public HttpResponseEncoder httpResponseEncoder() {
        return new HttpResponseEncoder();
    }

    @Bean(name = "httpRequestDecoder")
    public HttpRequestDecoder httpRequestDecoder() {
        return new HttpRequestDecoder();
    }

    /**
     * Necessary to make the Value annotations work.
     *
     * @return
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
