package cn.wtu.zld.entity;

import lombok.Data;

import java.util.List;

@Data
public class SendListMessage {
    private Integer messageStatus;
    private String fromName;
    private List<String> toName;
    private String message;
}
