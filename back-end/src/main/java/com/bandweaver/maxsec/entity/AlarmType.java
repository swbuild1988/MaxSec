package com.bandweaver.maxsec.entity;

import lombok.Data;

/**
 * 告警
 */
@Data
public class AlarmType {
    private Integer id;
    private String name;
    /**
     * 告警类别
     */
    private Integer category;
    /**
     * 告警级别
     */
    private Integer level;
    /**
     * 对象类型
     */
    private Integer objectType;
    /**
     * 告警描述
     */
    private String description;
}