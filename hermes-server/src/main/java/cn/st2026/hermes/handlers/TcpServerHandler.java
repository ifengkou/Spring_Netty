package cn.st2026.hermes.handlers;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Created by Sloong on 2015/9/2.
 */
@Component
@Qualifier("tcpServerHandler")
@Sharable
public class TcpServerHandler extends SimpleChannelInboundHandler<String> {
    @Override
    public void channelRead0(ChannelHandlerContext ctx,String msg){
        System.out.println(msg);
        ctx.channel().writeAndFlush(msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception{
        System.out.println("--Channel is active--");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception{
        System.out.println("--Channel is inactive--");
        super.channelInactive(ctx);
    }
}
