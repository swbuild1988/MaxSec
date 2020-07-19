package com.bandweaver.maxsec.constants;

import lombok.Getter;

/**
 * 告警类型
 */
public enum AlarmLevelEnum {

    Minor("提示", 1),
    Normal("一般", 2),
    Major("严重", 3),
    Critical("危急", 4);

    @Getter
    private String name;
    @Getter
    private int value;

    AlarmLevelEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

    /*
     * 根据value值获取enum
     */
    public static AlarmLevelEnum getEnum(int value) {
        for (AlarmLevelEnum tmp : AlarmLevelEnum.values()) {
            if (value == tmp.getValue())
                return tmp;
        }
        return null;
    }
}
