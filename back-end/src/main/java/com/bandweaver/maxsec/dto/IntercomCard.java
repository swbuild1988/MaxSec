package com.bandweaver.maxsec.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class IntercomCard implements Serializable {
    /**
     * 卡号
     */
    private String cardNo;
    /**
     * 工号
     */
    private String jobNo;
    /**
     * 人名
     */
    private String name;
    /**
     * 部门
     */
    private int department;
    /**
     * 有效期开始
     */
    private Date begin;
    /**
     * 有效期结束
     */
    private Date end;
    /**
     * 图片路径
     */
    private String fileName;
}
