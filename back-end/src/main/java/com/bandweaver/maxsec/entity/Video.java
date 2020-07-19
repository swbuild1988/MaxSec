package com.bandweaver.maxsec.entity;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * 视频
 */
@Data
public class Video extends MeasObject {

    /**
     * ip
     */
    private String ip;

    /**
     * 端口号
     */
    private Integer port;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 所属服务
     */
    private Integer serverId;

    /**
     * 通道号
     */
    private Integer channelNo;

    /**
     * 通道名称
     */
    private String channelName;

    /**
     * 默认预置位
     */
    private Integer defaultPreset;

    /**
     * 类型
     */
    private Integer vendor;

    public static Video fromMeasObj(MeasObject obj) {
        Video tmp = new Video();
        String str = JSONObject.toJSONString(obj);
        tmp = (Video) JSONObject.parseObject(str, Video.class);
        return tmp;
    }

    public String toJson() {
        return JSONObject.toJSONString(this);
    }

    public static Video fromJson(String json) {
        return (Video) JSONObject.parseObject(json, Video.class);
    }
}
