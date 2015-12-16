package cn.st2026.hermes.handlers;

import cn.st2026.hermes.cfg.MessageObject;
import com.google.gson.Gson;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpHeaders.Values;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

@Component
@Qualifier("httpHelloWorldServerHandler")
@ChannelHandler.Sharable
public class HttpHelloWorldServerHandler extends ChannelInboundHandlerAdapter {
    private static final byte[] CONTENT = { 'H', 'e', 'l', 'l', 'o', ' ', 'W', 'o', 'r', 'l', 'd' };
    private static final Gson GSON = new Gson();
    private static final Logger LOG = LoggerFactory.getLogger(HttpHelloWorldServerHandler.class);
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    //@Override
    public void channelRead1(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof HttpRequest) {
            HttpRequest req = (HttpRequest) msg;

            if (HttpHeaders.is100ContinueExpected(req)) {
                ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
            }
            boolean keepAlive = HttpHeaders.isKeepAlive(req);
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(CONTENT));
            response.headers().set(CONTENT_TYPE, "text/plain");
            response.headers().set(CONTENT_LENGTH, response.content().readableBytes());

            if (!keepAlive) {
                ctx.write(response).addListener(ChannelFutureListener.CLOSE);
            } else {
                response.headers().set(CONNECTION, Values.KEEP_ALIVE);
                ctx.write(response);
            }
        }
    }

    private void writeInvaildRequest(ChannelHandlerContext ctx){
        MessageObject messageObject = new MessageObject("FF",-1);
        String jsonStr = GSON.toJson(messageObject,MessageObject.class);
        writeJson(ctx,false,jsonStr);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx ,Object msg){
        if (msg instanceof HttpRequest) {
            HttpRequest req = (HttpRequest) msg;

            try {
                URI uri = new URI(req.getUri());
                String path = uri.getPath();
                if (path.equals("/favicon.ico")) {
                    return;
                }
                if (path.equals("/")) {
                    writeInvaildRequest(ctx);
                    return;
                }
                LOG.debug("request uri:{}", path);
            }catch (URISyntaxException e){
                LOG.error(e.getMessage());
            }

            if (HttpHeaders.is100ContinueExpected(req)) {
                ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
            }
            String param1 = "";

            if(req.getMethod() == HttpMethod.GET) {
                //处理get 参数
                QueryStringDecoder decoder = new QueryStringDecoder(req.getUri());
                Map<String, List<String>> params = decoder.parameters();
                List<String> param1s = params.get("param1");
                if (param1s != null && param1s.size() > 0) {
                    param1 = param1s.get(0);
                    LOG.debug("method=GET, param1={}", param1);
                }
            }
            //处理post 请求 参数
            if (req.getMethod() == HttpMethod.POST){
                /*HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(false), req);
                try {
                    InterfaceHttpData postData = decoder.getBodyHttpData("param1");
                    param1 = "";
                    if (postData.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                        Attribute attribute = (Attribute) postData;
                        param1 = attribute.getValue();
                        LOG.debug("method=POST, param1={}", param1);
                    }
                }catch (Exception e){
                    LOG.error(e.getMessage());
                }finally {
                    decoder.destroy();
                }*/
                QueryStringDecoder decoder = new QueryStringDecoder(req.getUri());
                Map<String, List<String>> params = decoder.parameters();
                List<String> param1s = params.get("param1");
                if (param1s != null && param1s.size() > 0) {
                    param1 = param1s.get(0);
                    LOG.debug("method=POST, param1={}", param1);
                }

            }


            boolean keepAlive = HttpHeaders.isKeepAlive(req);

            HashMap<String,String> map = new HashMap<String,String>();
            map.put("param1",param1);
            map.put("姓名", "申龙光");
            String json = GSON.toJson(map);

            writeJson(ctx,keepAlive,json);

        }
    }

    private void writeBytes(ChannelHandlerContext ctx, boolean keepAlive,byte[] content){
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(content));
        response.headers().set(CONTENT_TYPE, "text/plain");
        response.headers().set(CONTENT_LENGTH, response.content().readableBytes());

        if (!keepAlive) {
            ctx.write(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            response.headers().set(CONNECTION, Values.KEEP_ALIVE);
            ctx.write(response);
        }
    }

    private void writeText(ChannelHandlerContext ctx, boolean keepAlive,String text){
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.copiedBuffer(text, CharsetUtil.UTF_8));
        response.headers().set(CONTENT_TYPE, "text/plain");
        response.headers().set(CONTENT_LENGTH, response.content().readableBytes());

        if (!keepAlive) {
            ctx.write(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            response.headers().set(CONNECTION, Values.KEEP_ALIVE);
            ctx.write(response);
        }
    }

    private void writeJson(ChannelHandlerContext ctx,boolean keepAlive,String jsonStr){
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.copiedBuffer(jsonStr, CharsetUtil.UTF_8));

        response.headers().set(CONTENT_TYPE, "application/json; charset=utf-8");
        response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
        if (!keepAlive) {
            ctx.write(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            response.headers().set(CONNECTION, Values.KEEP_ALIVE);
            ctx.write(response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}