package cn.wtu.zld.utils;

import cn.wtu.zld.entity.Message;
import cn.wtu.zld.entity.Result;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 工具类，用来将消息进行JSON序列化
 * @author CodeDan
 * @time 2022年05月10日
 * **/
public class ResultToJsonUtils {

    public static String getResultByJson(int status, String fromName, String toName, String message){
        Message messageBody = new Message();
        messageBody.setFromName(fromName);
        messageBody.setToName(toName);
        messageBody.setMessage(message);
        Result result = new Result();
        result.setStatus(status);
        result.setResultBody(messageBody);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
