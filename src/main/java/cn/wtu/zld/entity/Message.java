package cn.wtu.zld.entity;

import lombok.Data;

@Data
public class Message {
    private Integer messageStatus;
    private String fromName;
    private String toName;
    private String message;
}
