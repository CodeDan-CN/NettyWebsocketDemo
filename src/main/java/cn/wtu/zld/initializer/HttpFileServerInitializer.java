package cn.wtu.zld.initializer;

import cn.wtu.zld.Handler.HttpFileServiceHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;


public class HttpFileServerInitializer extends ChannelInitializer<SocketChannel> {
    private int maxLength;

    public HttpFileServerInitializer(int maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        //获取通道
        ChannelPipeline pipeline = socketChannel.pipeline();
        //绑定Netty的Http编码解码类
        pipeline.addLast("NettyHttpServerCodec",new HttpServerCodec());
        //添加Post的编码解码类
        pipeline.addLast("NettyHttpObjectAggregator",new HttpObjectAggregator(maxLength));
        //绑定自定义Handler
        pipeline.addLast("HTTP-CHUCK",new ChunkedWriteHandler());
        pipeline.addLast("MyHttpServerHandler",new HttpFileServiceHandler());
    }
}