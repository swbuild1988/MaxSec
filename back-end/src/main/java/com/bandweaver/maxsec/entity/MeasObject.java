package com.bandweaver.maxsec.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 监测对象
 */
@Data
public class MeasObject implements Serializable {
    private Integer id;
    /**
     * 对象名
     */
    private String name;
    /**
     * 所处场站id
     */
    private Integer stationId;
    /**
     * 对象类型
     */
    private Integer objectType;
    /**
     * 数据类型
     */
    private Integer dataType;
    /**
     * 所属设备
     */
    private String equipmentSn;
    private Date createTime;
    /**
     * 是否布防
     */
    private Boolean actived;
}
