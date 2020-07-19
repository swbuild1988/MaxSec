package com.bandweaver.maxsec.dto;

import com.bandweaver.maxsec.constants.EquipmentEnum;
import com.bandweaver.maxsec.constants.EquipmentStateEnum;
import com.bandweaver.maxsec.entity.Equipment;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(value = {"handler"})
public class EquipmentDto extends Equipment {
    private String stationName;
    private String equipmentTypeName;
    private String equipmentStateName;

    public String getEquipmentTypeName() {
        return EquipmentEnum.getEnum(this.getEquipmentType().intValue()).getName();
    }

    public String getEquipmentStateName() {
        return EquipmentStateEnum.getEnum(this.getEquipmentState().intValue()).getName();
    }
}
