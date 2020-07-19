package com.bandweaver.maxsec.entity;

import lombok.Data;

import java.util.Date;

/**
 * 设备
 */
@Data
public class Equipment {
    private Integer id;
    private String name;
    /**
     * 序列号
     */
    private String sn;
    /**
     * 所属场站
     */
    private Integer stationId;
    /**
     * 设备类型
     */
    private Integer equipmentType;
    /**
     * 设备状态
     */
    private Integer equipmentState;
    private Date createTime;
}
