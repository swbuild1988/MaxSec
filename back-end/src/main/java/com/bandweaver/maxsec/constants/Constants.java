package com.bandweaver.maxsec.constants;

public class Constants {

    /**
     * 后端交换机名
     */
    public static final String FANOUT_EXCHANGE_BACK = "fanout.exchange.back";

    /**
     * 前端交换机名
     */
    public static final String FANOUT_EXCHANGE_FRONT= "fanout.exchange.front";

    /**
     * 后端告警队列
     */
    public static final String ALARM_QUEUE_BACK = "alarm.queue.back";

    /**
     * redis监测值类对象
     */
    public static final String REDIS_MEAS_OBJECT_VAL = "measobjectval";

    /**
     * redis存储的视频
     */
    public static final String REDIS_VIDEO = "video";

    /**
     * redis存储的视频服务
     */
    public static final String REDIS_VIDEOS_ERVER = "videoserver";

    /**
     * redis存储的视频服务会话 登录会话
     */
    public static final String REDIS_VIDEOSERVER_SESSION = "videoserver_session";

    /**
     * redis存储的视频联动编号 用于告警升级确认
     */
    public static final String REDIS_VIDEOLINKAGE_UPGRADE = "REDIS_VIDEOLINKAGE_UPGRADE";

    /**
     * redis存储的原始视频告警KEY 用于检测重复告警
     */
    public static final String REDIS_ORIGIN_VIDEO_ALARM = "REDIS_ORIGIN_VIDEO_ALARM";

    /**
     * redis存储的视频联动告警KEY 用于检测重复告警
     */
    public static final String REDIS_VIDEOLINKAGE_ALARM = "REDIS_VIDEOLINKAGE_ALARM";

    /**
     * redis存储的可视对讲长连接会话 用于呼叫
     */
    public static final String REDIS_INTERCOM_REMOTE_SESSION = "REDIS_INTERCOM_REMOTE_SESSION";
}
