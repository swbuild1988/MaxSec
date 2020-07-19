package com.bandweaver.maxsec.constants;

import lombok.Getter;

/**
 * 告警类型
 */
public enum MessageTypeEnum {

    VideoOriginAlarm("视频原始告警", 1),
    VideoAlarm("视频告警", 2),
    IntrusionAlarm("周界告警", 3),
    IntrusionUpgradeAlarm("周界升级告警", 4),
    IntercomAlarm("可视对讲告警", 5),
    IntercomGetFace("人脸下载完成", 6),
    IntercomSetFace("人脸设置完成", 7),
    IntercomGetCard("所有卡片下载完成", 8),
    IntercomSetCard("卡片设置完成", 9),
    IntercomDeleteCard("卡片删除完成", 10),
    VideoServerAlarm("视频服务告警", 11),
    CleanAlarm("清除告警", 12),
    IntercomOffline("可视对讲离线", 13);

    @Getter
    private String name;
    @Getter
    private int value;

    MessageTypeEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

    /*
     * 根据value值获取enum
     */
    public static MessageTypeEnum getEnum(int value) {
        for (MessageTypeEnum tmp : MessageTypeEnum.values()) {
            if (value == tmp.getValue())
                return tmp;
        }
        return null;
    }
}
