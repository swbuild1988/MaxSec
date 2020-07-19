package com.bandweaver.maxsec.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 告警
 */
@Data
public class Alarm implements Serializable {
    private Integer id;
    /**
     * 生成时间
     */
    private Date time;
    /**
     * 告警类型
     * 周界告警1-999
     * 视频告警1000-1999
     * 视频服务告警2000-2999
     * 可视对讲告警3000-3999
     * 高后果区告警4000-4999
     * 火灾消防告警5000-5999
     */
    private Integer alarmTypeId;
    /**
     * 监测对象Id
     */
    private Integer objId;
    /**
     * 告警等级
     */
    private Integer level;
    /**
     * 告警描述
     */
    private String description;
    /**
     * 是否清除
     */
    private Boolean cleaned;
    /**
     * 清除时间
     */
    private Date cleanedTime;
    /**
     * 告警源
     */
    private String source;

    /**
     * 时间戳
     * @return
     */
    public long getTimeStamp(){
        return this.getTime().getTime();
    }
}
