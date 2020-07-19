package com.bandweaver.maxsec.entity;

import lombok.Data;

@Data
public class Schedule {

    /**
     * 编号
     */
    private Integer id;

    /**
     * 名称
     */
    private String name;

    /**
     * 类名
     */
    private String className;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 触发条件
     */
    private String cron;

    /**
     * 是否激活
     */
    private Boolean actived;

    /**
     * 参数内容
     */
    private String param;

    /**
     * 类型
     */
    private Integer type;
}
