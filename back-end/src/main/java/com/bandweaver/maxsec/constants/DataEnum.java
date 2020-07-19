package com.bandweaver.maxsec.constants;

import lombok.Getter;

/**
 * 数据类型
 */
public enum DataEnum {

    AI("模拟量", 1),
    DI("开关量", 2),
    SI("状态量", 3),
    Video("视频流", 4);

    @Getter
    private String name;
    @Getter
    private int value;

    DataEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

    /**
     * 根据value值获取enum
     */
    public static DataEnum getEnum(int value) {
        for (DataEnum tmp : DataEnum.values()) {
            if (value == tmp.getValue())
                return tmp;
        }
        return null;
    }
}
