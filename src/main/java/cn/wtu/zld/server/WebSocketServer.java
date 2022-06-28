package cn.wtu.zld.server;

import cn.wtu.zld.config.WebSocketConfig;
import cn.wtu.zld.initializer.WebSocketServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;


/**
 * 通信服务类，用以构建WebSocket协议通信服务
 * @author CodeDan
 * @time 2022年04月11日
 * **/
public class WebSocketServer {

    private int port;
    private int selector;
    private int work;
    private int soBack;
    private String url;
    private int maxLength;


    public WebSocketServer(int port, int selector, int work, int soBack, String url, int maxLength) {
        this.port = port;
        this.selector = selector;
        this.work = work;
        this.soBack = soBack;
        this.url = url;
        this.maxLength = maxLength;
    }

    public void Listen(){
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(selector);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(work);
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        try{
            serverBootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    //backlog 用于构造服务端套接字ServerSocket对象，标识当服务器请求处理线程全满时，
                    //用于临时存放已完成三次握手的请求的队列的最大长度。
                    .option(ChannelOption.SO_BACKLOG,soBack)
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    .childHandler(new WebSocketServerInitializer(url,maxLength));
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
