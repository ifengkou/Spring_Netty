package cn.st2026.hermes.handlers;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpHeaders.Values;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;

/**
 * Created by Sloong on 2015/9/2.
 */
@Component
@Qualifier("httpServerInboundHandler")
@ChannelHandler.Sharable
public class HttpServerInboundHandler extends ChannelInboundHandlerAdapter {

    private HttpRequest request;
    @Override
    public void channelRead(ChannelHandlerContext ctx ,Object msg) throws Exception{
        if(msg instanceof HttpRequest){
            request = (HttpRequest) msg;
            String uri = request.getUri();
            System.out.println("Uri:"+uri);
        }
        if(msg instanceof HttpContent){
            HttpContent content = (HttpContent) msg;
            ByteBuf buf = content.content();
            System.out.println(buf.toString(CharsetUtil.UTF_8));
            buf.release();



        }

        String res = "hello world";

        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1
                ,HttpResponseStatus.OK
                ,Unpooled.wrappedBuffer(res.getBytes("UTF-8")));

        response.headers().set(CONTENT_TYPE,"text/plain");
        response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
        if(HttpHeaders.isKeepAlive(request)){
            response.headers().set(CONNECTION, Values.KEEP_ALIVE);
        }
        ctx.writeAndFlush(response);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception{
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println(cause.getMessage());
        ctx.close();
    }

    private void writeJSON(ChannelHandlerContext ctx, HttpResponseStatus status,
                           ByteBuf content/*, boolean isKeepAlive*/) {
        // TODO Auto-generated method stub
        if (ctx.channel().isWritable()) {
            FullHttpResponse msg = null;
            if (content != null) {
                msg = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, content);
                msg.headers().set(HttpHeaders.Names.CONTENT_TYPE, "application/json; charset=utf-8");
            } else {
                msg = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status);
            }
            if (msg.content() != null) {
                msg.headers().set(HttpHeaders.Names.CONTENT_LENGTH, msg.content().readableBytes());
            }

            //not keep-alive
            ctx.write(msg).addListener(ChannelFutureListener.CLOSE);
        }

    }
}
