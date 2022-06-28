package cn.wtu.zld.initializer;

import cn.wtu.zld.Handler.MyWebSocketServerHandler;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class WebSocketServerInitializer extends ChannelInitializer<SocketChannel> {

    private String url;

    private int maxLength;

    public WebSocketServerInitializer(String url, int maxLength) {
        this.url = url;
        this.maxLength = maxLength;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(maxLength));
        //指定WebSocket协议进行的服务名称，一般只有指定的服务才是WebSocket服务，
        //对应报文中的Sec-WebSocket-Protocol字段
        pipeline.addLast(new WebSocketServerProtocolHandler(url));
        pipeline.addLast(new IdleStateHandler(10,10,10, TimeUnit.SECONDS));
        pipeline.addLast(new MyWebSocketServerHandler());
    }
}
