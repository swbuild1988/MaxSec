package com.bandweaver.maxsec.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 告警
 */
@Data
public class VideoAlarm implements Serializable {
    /**
     * 生成时间
     */
    private Date time;
    /**
     * 通道IP
     */
    private String ip;
    /**
     * 通道IP
     */
    private Integer objectId;
    /**
     * 告警类型
     */
    private Integer alarmTypeId;
    /**
     * 告警图片
     */
    private String fileName;
    /**
     * 告警通道
     */
    private List<Integer> channelList;
    /**
     * 告警参数
     */
    private List<String> paras;
    /**
     * 是否清除
     */
    private Boolean cleaned;
}
