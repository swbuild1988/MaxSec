package com.bandweaver.maxsec.constants;

import lombok.Getter;

/**
 * 设备类型
 */
public enum EquipmentEnum {

    Video("视频", 1),
    Perimeter("周界安防", 2),
    Fire("消防", 3),
    FibreOptical("光纤", 4),
    Methane("甲烷", 5);

    @Getter
    private String name;
    @Getter
    private int value;

    EquipmentEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

    /**
     * 根据value值获取enum
     */
    public static EquipmentEnum getEnum(int value) {
        for (EquipmentEnum tmp : EquipmentEnum.values()) {
            if (value == tmp.getValue())
                return tmp;
        }
        return null;
    }
}
