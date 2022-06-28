package cn.wtu.zld.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 用作将Java对象转化为JSON对象
 * @author CodeDan
 * @time 2022年05月10日
 * **/
public class ObjectToJsonUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static String getJsonString(Object object){
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
