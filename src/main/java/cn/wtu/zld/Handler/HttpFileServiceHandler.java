package cn.wtu.zld.Handler;

import cn.wtu.zld.utils.MultipartRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.*;

import java.io.*;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class HttpFileServiceHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final String FILE_SAVE_PATH = "/Users/zld/local/HttpFile/";

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("连接成功"+ctx.channel().remoteAddress()+"对应channel"+ctx.channel().id().asLongText());

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        //更换客户端发来的Http数据类型为FullHttpRequest
        String[] strings = request.uri().split("\\?");
        //下载任务处理
        if (strings[0].equals("/downFile")) {
            System.out.println("格式"+request);
            Map<String,String> param = new HashMap<>();
            //这里要处理一下，选择性获取文件名称
            if(request.headers().get("fileName") == null){
                //如果在url中
                QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
                Set<Map.Entry<String, List<String>>> entries = decoder.parameters().entrySet();
                Iterator<Map.Entry<String, List<String>>> entryIterator = entries.iterator();
                while (entryIterator.hasNext()){
                    Map.Entry<String, List<String>> next = entryIterator.next();
                    param.put(next.getKey(),next.getValue().get(0));
                }
            }
            //判断文件目录中是否存在文件
            Set<Map.Entry<String, String>> entries = param.entrySet();
            for( Map.Entry<String, String> key : entries ){
                String filePath = FILE_SAVE_PATH + key.getValue();
                //首先判断文件存不存在与目录中，如果存在则发送文件信息，反之发送text信息为文件名+不存在。
                if( fileExist(filePath) ){
                    responseExportFile(ctx, FILE_SAVE_PATH, key.getValue());
                }else{
                    String result = key.getValue()+"不存在";
                    ByteBuf byteBuf = Unpooled.copiedBuffer(result.getBytes(StandardCharsets.UTF_8));
                    DefaultFullHttpResponse response =
                            new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK,byteBuf);
                    response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/plain");
                    response.headers().set(HttpHeaderNames.CONTENT_LENGTH,byteBuf.readableBytes());
                    ctx.writeAndFlush(response);
                }
            }
        }

        //上传接口处理
        if (request.uri().equals("/upLoadFile")) {
            System.out.println("上传请求格式如下：\n"+request+"\n   ");
            MultipartRequest MultipartBody = getMultipartBody(request);
            Map<String, FileUpload> fileUploads = MultipartBody.getFileUploads();
            //输出文件信息
            StringBuilder str = new StringBuilder();
            for (String key : fileUploads.keySet()) {
                //获取文件对象
                FileUpload file = fileUploads.get(key);
                System.out.println("fileName is" + file.getFile().getPath());
                //获取文件流
                FileInputStream in = new FileInputStream(file.getFile());
                FileChannel fileChannel = in.getChannel();
                //将文件保存起来
                saveFileToUrl(fileChannel,file.getFilename());
                str.append(" "+file.getFilename());
            }
            //输出参数信息
            Map<String, String> params = MultipartBody.getParams();
            //输出文件信息
            String result = str.toString() + "文件上传成功";
            ByteBuf byteBuf = Unpooled.copiedBuffer(result.getBytes(StandardCharsets.UTF_8));
            DefaultFullHttpResponse response =
                    new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK,byteBuf);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/plain");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH,byteBuf.readableBytes());
            ctx.writeAndFlush(response);

        }
    }


    public void saveFileToUrl(FileChannel fileChannel,String fileName){
        FileChannel out = null;
        try {
            RandomAccessFile rw = new RandomAccessFile("/Users/zld/local/HttpFile/" + fileName, "rw");
            out = rw.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            buffer.clear();
            int readLength = fileChannel.read(buffer);
            while( readLength != -1 ){
                buffer.flip();
                out.write(buffer);
                buffer.clear();
                readLength = fileChannel.read(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * <p>
     * 返回下载内容
     * </p>
     *
     * @param ctx
     * @author CodeDan
     */
    public static void responseExportFile(ChannelHandlerContext ctx, String path, String name) {
        String url = path+"/"+name;
        File file = new File(url);
        try {
            //读取文件
            final RandomAccessFile raf = new RandomAccessFile(file, "r");
            long fileLength = raf.length();
            //定义response对象
            HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            //设置请求头部
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, fileLength);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/octet-stream; charset=UTF-8");
            response.headers().add(HttpHeaderNames.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + URLEncoder.encode(file.getName(), "UTF-8") + "\";");
            ctx.write(response);
            //设置事件通知对象
            ChannelFuture sendFileFuture = ctx
                    .write(new DefaultFileRegion(raf.getChannel(), 0, fileLength), ctx.newProgressivePromise());
            sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
                //文件传输完成执行监听器
                @Override
                public void operationComplete(ChannelProgressiveFuture future)
                        throws Exception {
                    System.out.println("file {} transfer complete.");
                }
                //文件传输进度监听器
                @Override
                public void operationProgressed(ChannelProgressiveFuture future,
                                                long progress, long total) throws Exception {
                    if (total < 0) {
                        System.out.println("file {} transfer progress: {}");
                    } else {
                        System.out.println("file {} transfer progress: {}/{}");
                    }
                }
            });
            //刷新缓冲区数据，文件结束标志符
            ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 功能描述
     * <p>解析文件上传</p>
     *
     * @author CodeDan
     * @params [ctx, httpDecode]
     */
    private MultipartRequest getMultipartBody(FullHttpRequest request) {
        try {
            //创建HTTP对象工厂
            HttpDataFactory factory = new DefaultHttpDataFactory(true);
            //使用HTTP POST解码器(进行POST解码)
            HttpPostRequestDecoder httpDecoder = new HttpPostRequestDecoder(factory, request);
            httpDecoder.setDiscardThreshold(0);
            if (httpDecoder != null) {
                //获取HTTP请求对象
                final HttpContent chunk = (HttpContent) request;
                //加载对象到解码器。
                httpDecoder.offer(chunk);
                if (chunk instanceof LastHttpContent) {
                    //自定义对象bean
                    MultipartRequest multipartRequest = new MultipartRequest();
                    //存放文件对象
                    Map<String, FileUpload> fileUploads = new HashMap<>();
                    //存放参数对象
                    Map<String,String> body = new HashMap<>();
                    //通过迭代器获取HTTP的内容
                    List<InterfaceHttpData> interfaceHttpDataList = httpDecoder.getBodyHttpDatas();
                    System.out.println(interfaceHttpDataList);
                    for (InterfaceHttpData data : interfaceHttpDataList) {
                        //如果数据类型为文件类型，则保存到fileUploads对象中
                        if (data != null && InterfaceHttpData.HttpDataType.FileUpload.equals(data.getHttpDataType())) {
                            FileUpload fileUpload = (FileUpload) data;
                            fileUploads.put(data.getName(), fileUpload);
                        }
                        //如果数据类型为参数类型，则保存到body对象中
                        if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                            Attribute attribute = (Attribute) data;
                            body.put(attribute.getName(), attribute.getValue());
                        }
                    }
                    //存放文件信息
                    multipartRequest.setFileUploads(fileUploads);
                    //存放参数信息
                    multipartRequest.setParams(body);
                    return multipartRequest;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 功能描述
     * <p>判断文件是否存在</p>
     *
     * @author @CodeDan
     * @params [filePath]
     */
    public boolean fileExist(String filePath){
        File file = new File(filePath);
        return file.exists();
    }

}
