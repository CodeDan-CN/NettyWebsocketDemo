package cn.wtu.zld.config;

import cn.wtu.zld.utils.GetResourceValue;
import lombok.Data;

import java.util.ResourceBundle;


/**
 * ConfigPOJO类，用于信息服务器中WebSocket协议通信配置
 * @author CodeDan
 * @time 2022年04月15日
 * **/
@Data
public class WebSocketConfig {
    private static ResourceBundle resourceBundle = GetResourceValue.getResourceBundle();
    private static int port =
            Integer.parseInt(resourceBundle.getString("server.websocket.port"));
    private static int maxLength = Integer.parseInt(resourceBundle.getString("server.websocket.maxlength"));
    private static String url = resourceBundle.getString("server.websocket.url");
    private static int soBackLog = Integer.parseInt(resourceBundle.getString("server.websocket.so_back_log"));
    private static int selector = Integer.parseInt(resourceBundle.getString("server.websocket.selector"));
    private static int work = Integer.parseInt(resourceBundle.getString("server.websocket.work"));
    private static String flag = resourceBundle.getString("server.websocket.start");

    public static String getFlag() {
        return flag;
    }

    public static int getPort() {
        return port;
    }

    public static int getMaxLength() {
        return maxLength;
    }

    public static String getUrl() {
        return url;
    }

    public static int getSoBackLog() {
        return soBackLog;
    }

    public static int getSelector() {
        return selector;
    }

    public static int getWork() {
        return work;
    }
}

