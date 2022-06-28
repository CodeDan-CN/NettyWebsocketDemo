package cn.wtu.zld.server;

import cn.wtu.zld.initializer.HttpFileServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class HttpServerApplication {
    private int port;
    private int maxLength;
    private int selector;
    private int work;

    public HttpServerApplication(int port, int maxLength, int selector, int work) {
        this.port = port;
        this.maxLength = maxLength;
        this.selector = selector;
        this.work = work;
    }

    public void Listen(){

        NioEventLoopGroup bossGroup = new NioEventLoopGroup(selector);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(work);
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        try {
            serverBootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new HttpFileServerInitializer(maxLength));
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
