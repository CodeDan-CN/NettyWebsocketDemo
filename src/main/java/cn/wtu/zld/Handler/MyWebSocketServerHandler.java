package cn.wtu.zld.Handler;

import cn.wtu.zld.entity.Message;
import cn.wtu.zld.entity.Result;
import cn.wtu.zld.entity.SendListMessage;
import cn.wtu.zld.utils.ChannelManageUtil;
import cn.wtu.zld.utils.ResultToJsonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.*;

public class MyWebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    /**
     * 属于inboundHandler，当数据完成编解码后就会根据编解码对象到达此处，触发此方法
     * @param channelHandlerContext 通信通道
     * @param textWebSocketFrame WebSocket协议数据帧
     * */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
        //获取到传输的JSON数据
        String message = textWebSocketFrame.text();
        //进行JSON数据的转对象
        ObjectMapper objectMapper = new ObjectMapper();
        Result value = objectMapper.readValue(message, Result.class);
        String body = objectMapper.writeValueAsString(value.getResultBody());
        Message messageBody = null;
        SendListMessage sendListMessage = null;
        if( value.getStatus() == 210 ){
            //群发数据处理
            sendListMessage = objectMapper.readValue(body,SendListMessage.class);
            System.out.println(sendListMessage);
            sentMessageToOnlineList(sendListMessage.getMessageStatus(),sendListMessage.getFromName(),sendListMessage.getToName(),null);
        }else if( value.getStatus() == 220 ){
            //单发数据处理
            messageBody = objectMapper.readValue(body,Message.class);
            if( messageBody.getMessageStatus() != 120 ){
                System.out.println(messageBody);
                sentMessageToFriend(messageBody.getMessageStatus(),messageBody.getFromName(),messageBody.getToName(),messageBody.getMessage());
            }
        }else{
            //注册处理
            messageBody = objectMapper.readValue(body,Message.class);
            ChannelManageUtil.add(messageBody.getFromName(),channelHandlerContext);
        }
        //进行心跳的重置
        ChannelManageUtil.initPingNumber(channelHandlerContext.channel().id().asLongText());


    }

    /**
     * 当客户端关闭通道时，触发此方法
     * @param ctx 通信通道
     * */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        //断开流程
        ChannelManageUtil.delete(ctx.channel().id().asLongText());
        //进行心跳删除
        ChannelManageUtil.deletePingNumber(ctx.channel().id().asLongText());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        String ctxId = ctx.channel().id().asLongText();
        if( evt instanceof IdleStateEvent){
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            IdleState state = idleStateEvent.state();
            switch (state){
                case READER_IDLE:
                    ;break;
                case WRITER_IDLE:
                    ;break;
                case ALL_IDLE:
                    //检测心跳包
                    if( ChannelManageUtil.getPingNumber(ctxId) >= 60 ){
                        ctx.close();
                        //继续心跳的删除
                        ChannelManageUtil.deletePingNumber(ctxId);
                    }
                    System.out.println("读写超时");
                    //发送心跳包
                    String sendMessage = ResultToJsonUtils.getResultByJson(120,null,null,null);
                    ctx.writeAndFlush(new TextWebSocketFrame(sendMessage));
                    //增加心跳包次数
                    ChannelManageUtil.addPingNumber(ctxId);
                    break;
            }
        }
    }

    /**
     * 封装的一个向在线好友，单发信息的功能
     * @param status
     * @param fromName
     * @param message
     * */
    private void sentMessageToFriend(int status, String fromName, String toName, String message){
        if( toName == null ){
            return;
        }
        if( ChannelManageUtil.containsKey(toName) ){
            String messageBody = ResultToJsonUtils.getResultByJson(status,fromName,toName,message);
            ChannelManageUtil.get(toName).writeAndFlush(new TextWebSocketFrame(messageBody));
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
                ChannelManageUtil.get(sendAccount).writeAndFlush(new TextWebSocketFrame(messageBody));
            }
        }
    }


}
