package cn.wtu.zld.config;

import cn.wtu.zld.utils.GetResourceValue;

import java.util.ResourceBundle;

/**
 * ConfigPOJO类，用于信息服务器中Kcp协议通信配置
 * @author CodeDan
 * @time 2022年04月15日
 * **/
public class KcpConfig {

    private static ResourceBundle resourceBundle = GetResourceValue.getResourceBundle();
    private static int port =
            Integer.parseInt(resourceBundle.getString("server.kcp.port"));
    private static String flag = resourceBundle.getString("server.kcp.start");

    public static String getFlag() {
        return flag;
    }

    public static int getPort() {
        return port;
    }
}
