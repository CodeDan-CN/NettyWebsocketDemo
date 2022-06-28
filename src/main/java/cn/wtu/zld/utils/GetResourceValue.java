package cn.wtu.zld.utils;

import java.util.ResourceBundle;


/**
 * 配置获取工具类，用以获取指定配置文件下的配置信息
 * @author CodeDan
 * @time 2022年04月15日
 * **/
public class GetResourceValue {

    private static ResourceBundle resourceBundle = ResourceBundle.getBundle("MessageServer");

    public static ResourceBundle getResourceBundle(){
        return resourceBundle;
    }
}
