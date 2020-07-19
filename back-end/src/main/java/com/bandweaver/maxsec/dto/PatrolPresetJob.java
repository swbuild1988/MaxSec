package com.bandweaver.maxsec.dto;

import com.bandweaver.maxsec.constants.ScheduleTypeEnum;
import lombok.Data;

@Data
public class PatrolPresetJob {
    /**
     * 视频编号
     */
    private Integer videoId;
    /**
     * 巡检预置位 不包括默认预置位
     */
    private Integer[] presets;

    private Integer stayTime;
}
