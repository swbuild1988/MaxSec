package com.bandweaver.maxsec.entity;

import lombok.Data;

import java.util.Date;

/**
 * 地图信息
 */
@Data
public class CustomMap {
    private Integer id;
    private String name;
    private String geomaps_str;
    private Date createTime;
}
