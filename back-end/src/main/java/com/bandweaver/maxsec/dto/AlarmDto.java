package com.bandweaver.maxsec.dto;

import com.bandweaver.maxsec.constants.AlarmLevelEnum;
import com.bandweaver.maxsec.entity.Alarm;
import com.bandweaver.maxsec.entity.AlarmType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(value = {"handler"})
public class AlarmDto extends Alarm {
    private String objectName;
    private Integer stationId;
    private String stationName;
    private String levelName;
    private AlarmType alarmType;

    public long getTimeStamp(){
        return this.getTime().getTime();
    }


    public String getLevelName() {
        return AlarmLevelEnum.getEnum(this.getLevel().intValue()).getName();
    }
}
