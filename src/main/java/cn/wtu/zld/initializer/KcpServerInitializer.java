package cn.wtu.zld.initializer;

import cn.wtu.zld.Handler.KcpRttServerHandler;
import io.jpower.kcp.netty.UkcpChannel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;

public class KcpServerInitializer extends ChannelInitializer<UkcpChannel> {
    @Override
    protected void initChannel(UkcpChannel ukcpChannel) throws Exception {
        ChannelPipeline pipeline = ukcpChannel.pipeline();
        pipeline.addLast(new KcpRttServerHandler());
    }
}
