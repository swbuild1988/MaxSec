package com.bandweaver.maxsec.dto;

import com.bandweaver.maxsec.constants.MessageTypeEnum;
import lombok.Data;

import java.io.Serializable;

@Data
public class Message implements Serializable {
    /**
     * 消息类型
     */
    private MessageTypeEnum type;
    /**
     * 消息内容
     */
    private Object body;
}
