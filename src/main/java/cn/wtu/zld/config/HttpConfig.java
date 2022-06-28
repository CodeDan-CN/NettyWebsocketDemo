package cn.wtu.zld.config;

import cn.wtu.zld.utils.GetResourceValue;

import java.util.ResourceBundle;

public class HttpConfig {
    private static ResourceBundle resourceBundle = GetResourceValue.getResourceBundle();
    private static int port =
            Integer.parseInt(resourceBundle.getString("server.http.port"));
    private static int maxLength = Integer.parseInt(resourceBundle.getString("server.http.maxlength"));
    private static int selector = Integer.parseInt(resourceBundle.getString("server.http.selector"));
    private static int work = Integer.parseInt(resourceBundle.getString("server.http.work"));
    private static String flag = resourceBundle.getString("server.http.start");

    public static String getFlag() {
        return flag;
    }

    public static int getPort() {
        return port;
    }

    public static int getMaxLength() {
        return maxLength;
    }

    public static int getSelector() {
        return selector;
    }

    public static int getWork() {
        return work;
    }
}
