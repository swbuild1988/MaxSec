package com.bandweaver.maxsec.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 场站对象
 */
@Data
public class Station implements Serializable {
    private Integer id;
    /**
     * 场站名
     */
    private String name;
    /**
     * 位置信息，GPS或者地图坐标，通过','分割
     */
    private String position;
    /**
     * 所属管理处
     */
    private Integer managementId;
    /**
     * 图标
     */
    private Integer icon;
    /**
     * 创建时间
     */
    private Date createTime;
}
