package cn.st2026.hermes.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@Component
@Qualifier("httpServerHandler")
@ChannelHandler.Sharable
public class HttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    private final static Logger LOG = LoggerFactory.getLogger(HttpServerHandler.class);
    private HttpRequest request;
    private ByteBuf buffer_body = UnpooledByteBufAllocator.DEFAULT.buffer();
    /*
     * for debug
     */
    private StringBuffer sb_debug = new StringBuffer();

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        loglog(ctx, "[channelRegistered]");
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    public void channelRead0(ChannelHandlerContext ctx, HttpObject msg){
        try {
            if ((msg instanceof HttpMessage) && HttpHeaders.is100ContinueExpected((HttpMessage)msg)) {
                ctx.write(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE));
            }
            if (msg instanceof HttpRequest) {
                this.request = (HttpRequest)msg;
                sb_debug.append("\n>> HTTP REQUEST -----------\n");
                sb_debug.append(this.request.getProtocolVersion().toString())
                        .append(" ").append(this.request.getMethod().name())
                        .append(" ").append(this.request.getUri());
                sb_debug.append("\n");
                HttpHeaders headers = this.request.headers();
                if (!headers.isEmpty()) {
                    for (Map.Entry<String, String> header : headers) {
                        sb_debug.append(header.getKey()).append(": ").append(header.getValue()).append("\n");
                    }
                }
                sb_debug.append("\n");
            } else if (msg instanceof HttpContent) {
                HttpContent content = (HttpContent) msg;
                ByteBuf thisContent = content.content();
                if (thisContent.isReadable()) {
                    buffer_body.writeBytes(thisContent);
                }
                if (msg instanceof LastHttpContent) {
                    sb_debug.append(buffer_body.toString(CharsetUtil.UTF_8));
                    LastHttpContent trailer = (LastHttpContent) msg;
                    if (!trailer.trailingHeaders().isEmpty()) {
                        for (String name : trailer.trailingHeaders().names()) {
                            sb_debug.append(name).append("=");
                            for (String value : trailer.trailingHeaders().getAll(name)) {
                                sb_debug.append(value).append(",");
                            }
                            sb_debug.append("\n\n");
                        }
                    }
                    sb_debug.append("\n<< HTTP REQUEST -----------");
                }
            }
        } catch (Exception e) {

        } finally {

        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // TODO Auto-generated method stub
        super.channelReadComplete(ctx);

        //this point is Business logic started
        writeJSON(ctx, HttpResponseStatus.OK, Unpooled.copiedBuffer("{result:true,message:'hello'}", CharsetUtil.UTF_8));
        ctx.flush();
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
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
            throws Exception {
        // TODO Auto-generated method stub
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx)
            throws Exception {
        // TODO Auto-generated method stub
        super.channelWritabilityChanged(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        // TODO Auto-generated method stub
        super.exceptionCaught(ctx, cause);
    }
    private static void loglog(ChannelHandlerContext ctx, String message) {
        if (!LOG.isDebugEnabled())
            return;
        // debug
        StringBuilder sb = new StringBuilder(message);
        sb.append("\n").append("name=").append(ctx.name());
        sb.append(", addr=").append(ctx.channel().localAddress().toString());
        Map<ChannelOption<?>, Object> options = ctx.channel().config().getOptions();
        sb.append("\n[ch.opts]");
        for (Map.Entry<ChannelOption<?>, Object> option : options.entrySet()) {
            sb.append(" <").append(option.getKey().name()).append(":").append(option.getValue().toString()).append(
                    ">\n");
        }
        LOG.debug(sb.toString());
    }
}