package cn.st2026.hermes.client.http;


import cn.st2026.hermes.client.handler.HttpClientInboundHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Created by Sloong on 2015/9/9.
 */
public class HttpClient {
    private String buildParams(Map<String,String> param){
        StringBuffer sb = new StringBuffer();
        Set<String> keySet = param.keySet();
        for (String key : keySet) {
            sb.append("&").append(key).append("=").append(param.get(key));
        }
        if(sb.length()>1) {
            return sb.substring(1);
        }
        return "";
    }
    public void connect(String host, int port) throws Exception {
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    // 客户端接收到的是httpResponse响应，所以要使用HttpResponseDecoder进行解码
                    ch.pipeline().addLast(new HttpResponseDecoder());
                    // 客户端发送的是httprequest，所以要使用HttpRequestEncoder进行编码
                    ch.pipeline().addLast(new HttpRequestEncoder());
                    ch.pipeline().addLast(new HttpClientInboundHandler());
                }
            });

            // Start the client.
            ChannelFuture f = b.connect(host, port).sync();




            String url = "http://"+host+":"+port+"/test";
            /*Map<String,String> params = new HashMap<>();
            params.put("param1","'helloparam1'");
            //params.put("param2","'hello param2'");
            String urlP = buildParams(params);
            if(urlP.length()>0){
                url = url + "?" + urlP;
            }*/

            QueryStringEncoder encoder = new QueryStringEncoder(url);
            // add Form attribute
            encoder.addParam("getform", "GET");
            encoder.addParam("param1", "first value");
            encoder.addParam("secondinfo", "secondvalue");
            // not the big one since it is not compatible with GET size
            // encoder.addParam("thirdinfo", textArea);
            encoder.addParam("thirdinfo", "third value\r\ntest second line\r\n\r\nnew line\r\n");
            encoder.addParam("Send", "Send");

            //URI uriGet = new URI(encoder.toString());

            URI uri = new URI(encoder.toString());
            String msg = "param1=Are you ok?";
            DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST,
                    uri.toASCIIString(), Unpooled.wrappedBuffer(msg.getBytes("UTF-8")));
            // 构建http请求
            request.headers().set(HttpHeaders.Names.HOST, host);
            request.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
            request.headers().set(HttpHeaders.Names.CONTENT_LENGTH, request.content().readableBytes());
            //request.headers().set(HttpHeaders.Names.PRAGMA)
            //request.headers().set(HttpHeaders.Names.,)
            // 发送http请求pragma
            f.channel().write(request);
            f.channel().flush();
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }

    }
    public static void main(String[] args){
        try {
            HttpClient client = new HttpClient();
            client.connect("127.0.0.1", 9112);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
