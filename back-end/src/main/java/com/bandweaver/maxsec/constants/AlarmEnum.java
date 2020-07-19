package com.bandweaver.maxsec.constants;

import lombok.Getter;

/**
 * 告警类型
 */
public enum AlarmEnum {

    Video("视频告警", 1),
    Perimeter("周界入侵", 2),
    Fire("消防告警", 3),
    FibreOptical("光纤预计", 4),
    Methane("激光甲烷", 5);

    @Getter
    private String name;
    @Getter
    private int value;

    AlarmEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

    /**
     * 根据value值获取enum
     */
    public static AlarmEnum getEnum(int value) {
        for (AlarmEnum tmp : AlarmEnum.values()) {
            if (value == tmp.getValue())
                return tmp;
        }
        return null;
    }
}
