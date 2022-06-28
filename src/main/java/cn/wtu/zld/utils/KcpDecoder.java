package cn.wtu.zld.utils;

import io.netty.buffer.ByteBuf;


/**
 * 解码工具类，用于KCP协议格式数据的解码
 * @author CodeDan
 * @time 2022年04月15日
 * **/
public class KcpDecoder {

    /**
     * count+timestamp+dataLen+data
     * @param msg
     * @return
     */
    public static String decoder(Object msg){
        ByteBuf buf = (ByteBuf) msg;
        short curCount = buf.readShort();
        long readInt = buf.readInt();
        short datalen = buf.readShort();
        byte[] bytes = new byte[datalen];
        buf.readBytes(bytes);
        String result = new String(bytes);
        buf.resetReaderIndex();
        return result;
    }
}
