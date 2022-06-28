package cn.wtu.zld.server;

import cn.wtu.zld.initializer.KcpServerInitializer;
import io.jpower.kcp.netty.ChannelOptionHelper;
import io.jpower.kcp.netty.UkcpChannelOption;
import io.jpower.kcp.netty.UkcpServerChannel;
import io.netty.bootstrap.UkcpServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

/**
 * Measures RTT(Round-trip time) for KCP.
 * <p>
 * Receives a message from client and sends a response.
 *
 * @author <a href="mailto:szhnet@gmail.com">szh</a>
 */
public class KcpRttServer {

    private int port ;

    public KcpRttServer(int port) {
        this.port = port;
    }

    public void listen() {
        // Configure the server.
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            UkcpServerBootstrap b = new UkcpServerBootstrap();
            b.group(group)
                    .channel(UkcpServerChannel.class)
                    .childHandler(new KcpServerInitializer());
            ChannelOptionHelper.nodelay(b, true, 20, 2, true)
                    .childOption(UkcpChannelOption.UKCP_MTU, 512);

            // Start the server.
            ChannelFuture f = b.bind(port).sync();

            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();
        }  catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // Shut down all event loops to terminate all threads.
            group.shutdownGracefully();
        }
    }

}
