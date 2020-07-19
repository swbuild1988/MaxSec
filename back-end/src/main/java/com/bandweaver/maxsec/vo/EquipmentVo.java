package com.bandweaver.maxsec.vo;

import lombok.Data;

import java.util.List;

@Data
public class EquipmentVo {

    private Integer stationId;

    private List<Integer> stationIds;

    private Integer equipmentType;

    private Integer equipmentState;
}
