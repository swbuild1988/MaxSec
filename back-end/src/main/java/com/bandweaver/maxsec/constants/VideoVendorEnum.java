package com.bandweaver.maxsec.constants;

import lombok.Getter;

/**
 * 视频服务类型
 */
public enum VideoVendorEnum {

    HikNvr("海康", 1),

    Uniview("宇视", 2),

    Dahua("大华", 3);

    @Getter
    private String name;
    @Getter
    private int value;

    VideoVendorEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

    /**
     * 根据value值获取enum
     */
    public static VideoVendorEnum getEnum(int value) {
        for (VideoVendorEnum tmp : VideoVendorEnum.values()) {
            if (value == tmp.getValue())
                return tmp;
        }
        return null;
    }
}
