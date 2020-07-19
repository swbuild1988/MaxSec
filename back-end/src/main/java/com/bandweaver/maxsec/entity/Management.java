package com.bandweaver.maxsec.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 管理部门
 */
@Data
public class Management implements Serializable {
    private Integer id;
    /**
     * 名称
     */
    private String name;
    /**
     * 地理位置坐标
     */
    private String position;
    /**
     * 等级
     */
    private Integer level;
    /**
     * 父节点id
     */
    private Integer fatherId;
    /**
     * 是否为叶子
     */
    private boolean leaf;
    /**
     * 地图名称
     */
    private String map;
    /**
     * 创建时间
     */
    private Date createTime;
}
