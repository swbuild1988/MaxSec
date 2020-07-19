package com.bandweaver.maxsec.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 监测数值
 */
@Data
public class MeasValue implements Serializable {

    /**
     * 对象序列号
     */
    private Integer id;

    /**
     * 值
     */
    private Double value;

    /**
     * 采集时间
     */
    private Date time;
}
