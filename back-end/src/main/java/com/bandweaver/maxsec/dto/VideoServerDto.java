package com.bandweaver.maxsec.dto;

import com.bandweaver.maxsec.entity.Video;
import com.bandweaver.maxsec.entity.VideoServer;
import lombok.Data;

import java.util.List;

@Data
public class VideoServerDto extends VideoServer {
    /**
     * 旗下所有的视频
     */
    private List<Video> videos;
}
