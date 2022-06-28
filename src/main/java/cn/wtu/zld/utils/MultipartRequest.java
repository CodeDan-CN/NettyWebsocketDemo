package cn.wtu.zld.utils;

import io.netty.handler.codec.http.multipart.FileUpload;
import lombok.Data;

import java.util.Map;

/**
 * <p>请求对象</p>
 *
 * @author CodeDan
 */
@Data
public class MultipartRequest {
    private Map<String, FileUpload> fileUploads;
    private Map<String,String> params;

}
