package com.bandweaver.maxsec.constants;

import lombok.Getter;

/**
 * 设备状态类型
 */
public enum EquipmentStateEnum {

    OnLine("在线", 1),
    OffLine("离线", 2),
    Fault("故障", 3);

    @Getter
    private String name;
    @Getter
    private int value;

    EquipmentStateEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

    /**
     * 根据value值获取enum
     */
    public static EquipmentStateEnum getEnum(int value) {
        for (EquipmentStateEnum tmp : EquipmentStateEnum.values()) {
            if (value == tmp.getValue())
                return tmp;
        }
        return null;
    }
}
