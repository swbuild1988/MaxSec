package com.bandweaver.maxsec.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class AlarmVo {
    private Integer objId;

    private Integer stationId;

    private List<Integer> stationIds;

    private Integer type;

    private Integer alarmType;

    private Integer level;

    private Boolean cleaned;

    private Date startTime;

    private Date endTime;
}
