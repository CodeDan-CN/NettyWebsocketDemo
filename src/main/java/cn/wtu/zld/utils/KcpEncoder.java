package cn.wtu.zld.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;


/**
 * 编码工具类，用于KCP协议格式数据的编码
 * @author CodeDan
 * @time 2022年04月15日
 * **/
public class KcpEncoder {

    /**
     * count+timestamp+dataLen+data
     * @param count
     * @return
     */
    public static ByteBuf Encoder(int count, ByteBuf data) {
        ByteBuf buf = Unpooled.buffer(10);
        buf.writeShort(count);
        buf.writeInt((int) (System.currentTimeMillis() - System.currentTimeMillis()));
        int dataLen = data.readableBytes();
        buf.writeShort(dataLen);
        buf.writeBytes(data, data.readerIndex(), dataLen);
        return buf;
    }

}
