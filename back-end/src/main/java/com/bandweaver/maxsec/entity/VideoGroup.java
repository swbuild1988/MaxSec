package com.bandweaver.maxsec.entity;

import lombok.Data;

import java.util.Date;

/**
 * 视频组
 */
@Data
public class VideoGroup {
    private Integer id;

    private String name;

    private Integer stationId;

    private String videos;

    private Date createTime;
}
