package com.bandweaver.maxsec.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 场站关联
 */
@Data
public class StationLink implements Serializable {
    private Integer id;
    /**
     * 起始
     */
    private Integer source;
    /**
     * 目标
     */
    private Integer target;
    /**
     * 类型
     */
    private Integer type;
}
