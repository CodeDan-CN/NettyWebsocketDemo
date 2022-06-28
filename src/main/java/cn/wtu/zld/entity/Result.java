package cn.wtu.zld.entity;

import lombok.Data;

/**
 * POJO类，用于客户端与信息服务器间的通信，采用JSON序列化进行网络传输
 * @author CodeDan
 * @time 2022年04月11日
 * **/
@Data
public class Result {
    private Integer status;
    private Object resultBody;
}
