package com.bandweaver.maxsec.entity;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * 视频服务
 */
@Data
public class VideoServer extends MeasObject {

    /**
     * 通道数
     */
    private Integer portNum;

    /**
     * ip
     */
    private String ip;

    /**
     * 端口号
     */
    private Integer port;

    /**
     * username
     */
    private String username;

    /**
     * password
     */
    private String password;

    /**
     * 类型
     */
    private Integer vendor;

    public static VideoServer fromMeasObj(MeasObject obj) {
        VideoServer tmp = new VideoServer();
        String str = JSONObject.toJSONString(obj);
        tmp = (VideoServer) JSONObject.parseObject(str, VideoServer.class);
        return tmp;
    }
}
