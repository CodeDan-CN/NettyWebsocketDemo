package cn.wtu.zld;

import cn.wtu.zld.config.HttpConfig;
import cn.wtu.zld.config.KcpConfig;
import cn.wtu.zld.config.WebSocketConfig;
import cn.wtu.zld.server.HttpServerApplication;
import cn.wtu.zld.server.KcpRttServer;
import cn.wtu.zld.server.WebSocketServer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 服务器启动类，用来启动信息服务器
 * @author CodeDan
 * @time 2022年05月10日
 * **/
public class MessageServerApplication {

    //使用线程池池化技术
    private static ExecutorService executorService = Executors.newFixedThreadPool(3);

    public static void main(String[] args) throws Exception {
        if( "true".equals(WebSocketConfig.getFlag()) && "true".equals(KcpConfig.getFlag()) ){
            throw new Exception();
        }
        //启动WebSocket协议服务
        if( "true".equals(WebSocketConfig.getFlag())  ){
            executorService.execute(()->{
                new WebSocketServer(WebSocketConfig.getPort(),
                        WebSocketConfig.getSelector(),
                        WebSocketConfig.getWork(),
                        WebSocketConfig.getSoBackLog(),
                        WebSocketConfig.getUrl(),
                        WebSocketConfig.getMaxLength()).Listen();
            });
        }
        //启动Kcp协议服务
        if( "true".equals(KcpConfig.getFlag()) ){
            executorService.execute(()->{
                new KcpRttServer(KcpConfig.getPort()).listen();
            });
        }
        //启动Http协议服务
        if( "true".equals(HttpConfig.getFlag()) ){
            new HttpServerApplication(HttpConfig.getPort(),HttpConfig.getMaxLength(),HttpConfig.getSelector(),HttpConfig.getWork()).Listen();
        }
    }
}
