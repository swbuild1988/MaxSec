package com.bandweaver.maxsec.dto;

import com.bandweaver.maxsec.entity.Video;
import com.bandweaver.maxsec.entity.VideoServer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(value = { "handler" })
public class VideoDto extends Video {
    /**
     * 所属视频服务
     */
    private VideoServer videoServer;
}
