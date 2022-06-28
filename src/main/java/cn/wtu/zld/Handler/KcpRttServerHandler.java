package cn.wtu.zld.Handler;

import cn.wtu.zld.entity.Message;
import cn.wtu.zld.entity.Result;
import cn.wtu.zld.entity.SendListMessage;
import cn.wtu.zld.server.KcpRttServer;
import cn.wtu.zld.utils.ChannelManageUtil;
import cn.wtu.zld.utils.KcpDecoder;
import cn.wtu.zld.utils.KcpEncoder;
import cn.wtu.zld.utils.ResultToJsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jpower.kcp.netty.UkcpChannel;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;

/**
 * @author @CodeDan
 */
public class KcpRttServerHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        UkcpChannel kcpCh = (UkcpChannel) ctx.channel();
        kcpCh.conv(10);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws JsonProcessingException {
        String message = KcpDecoder.decoder(msg);
        ObjectMapper objectMapper = new ObjectMapper();
        Result value = objectMapper.readValue(message, Result.class);
        String body = objectMapper.writeValueAsString(value.getResultBody());
        Message messageBody = null;
        SendListMessage sendListMessage = null;
        if( value.getStatus() == 210 ){
            //群发数据处理
            sendListMessage = objectMapper.readValue(body,SendListMessage.class);
            sentMessageToOnlineList(sendListMessage.getMessageStatus(),sendListMessage.getFromName(),sendListMessage.getToName(),sendListMessage.getMessage());
        }else if( value.getStatus() == 220 ){
            //单发数据处理
            messageBody = objectMapper.readValue(body,Message.class);
            sentMessageToFriend(messageBody.getMessageStatus(),messageBody.getFromName(),messageBody.getToName(),messageBody.getMessage());
        }else{
            //注册处理
            messageBody = objectMapper.readValue(body,Message.class);
            ChannelManageUtil.add(messageBody.getFromName(),ctx);
        }


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        ChannelManageUtil.delete(ctx.channel().id().asLongText());
        ctx.close();
    }


    /**
     * 封装的一个向在线好友，单发信息的功能
     * @param status
     * @param fromName
     * @param message
     * */
    private void sentMessageToFriend(int status, String fromName, String toName, String message){
        if( ChannelManageUtil.containsKey(toName) ){
            String messageBody = ResultToJsonUtils.getResultByJson(status,fromName,toName,message);
            ByteBuf buf = KcpEncoder.Encoder(1, Unpooled.copiedBuffer(messageBody.getBytes(StandardCharsets.UTF_8)));
            ChannelManageUtil.get(toName).writeAndFlush(buf);
        }
    }

    /**
     * 封装的一个向在线好友，群发信息的功能
     * @param status
     * @param toName
     * @param message
     * */
    private void sentMessageToOnlineList(int status, String userAccount, List<String> toName, String message){
        Iterator<String> iterator = toName.iterator();
        while( iterator.hasNext() ){
            String sendAccount = iterator.next();
            if( ChannelManageUtil.containsKey(sendAccount) ){
                String messageBody = ResultToJsonUtils.getResultByJson(status,userAccount,sendAccount,message);
                ByteBuf buf = KcpEncoder.Encoder(1, Unpooled.copiedBuffer(messageBody.getBytes(StandardCharsets.UTF_8)));
                ChannelManageUtil.get(sendAccount).writeAndFlush(buf);
            }
        }
    }
}
