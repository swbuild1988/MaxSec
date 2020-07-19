package com.bandweaver.maxsec.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 网络节点
 */
@Data
public class NetNode implements Serializable {
    private Integer id;
    private String name;
    /**
     * 父节点Id
     */
    private Integer fatherId;
    /**
     * 第几级
     */
    private Integer level;
    /**
     * 所属类型（场站、视频服务、视频等）
     */
    private String type;
    /**
     * 所在表id
     */
    private Integer usedId;
    /**
     * 是否和父节点相连接
     */
    private boolean connected;
    /**
     * 是否为根节点
     */
    private boolean root;
    /**
     * 创建时间
     */
    private Date createTime;
}
