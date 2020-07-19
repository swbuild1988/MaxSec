package com.bandweaver.maxsec.constants;

import lombok.Getter;

/**
 * 告警类型
 */
public enum AlarmCategoryEnum {

    Device("设备", 1),
    Biz("业务", 2);

    @Getter
    private String name;
    @Getter
    private int value;

    AlarmCategoryEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

    /*
     * 根据value值获取enum
     */
    public static AlarmCategoryEnum getEnum(int value) {
        for (AlarmCategoryEnum tmp : AlarmCategoryEnum.values()) {
            if (value == tmp.getValue())
                return tmp;
        }
        return null;
    }
}
