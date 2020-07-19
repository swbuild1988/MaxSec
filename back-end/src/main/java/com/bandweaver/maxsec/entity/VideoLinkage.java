package com.bandweaver.maxsec.entity;

import lombok.Data;

@Data
public class VideoLinkage {

    /**
     * 编号
     */
    private Integer id;

    /**
     * 对象编号
     */
    private Integer objId;

    /**
     * 联动区间开始
     */
    private Integer startLocation;

    /**
     * 联动区间结束
     */
    private Integer endLocation;

    /**
     * 视频对象编号
     */
    private Integer videoId;

    /**
     * 联动预置位
     */
    private Integer preset;

    /**
     * 是否激活
     */
    private Boolean actived;

}
