package com.bandweaver.maxsec.constants;

import lombok.Getter;

/**
 * 数据类型
 */
public enum ObjectEnum {

    Video("视频", 1),
    HighVideo("高后果视频", 2),
    Intrusion("周界入侵", 3),
    Methane("甲烷", 4),
    Intercom("可视对讲", 5),
    VideoServer("视频服务", 6);

    @Getter
    private String name;
    @Getter
    private int value;

    ObjectEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

    /**
     * 根据value值获取enum
     */
    public static ObjectEnum getEnum(int value) {
        for (ObjectEnum tmp : ObjectEnum.values()) {
            if (value == tmp.getValue())
                return tmp;
        }
        return null;
    }
}
