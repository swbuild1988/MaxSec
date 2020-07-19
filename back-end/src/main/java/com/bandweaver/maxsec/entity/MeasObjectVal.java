package com.bandweaver.maxsec.entity;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.Date;

/**
 * 带值的检测对象（包括AI，DI，SI等）
 */
@Data
public class MeasObjectVal extends MeasObject {

    /**
     * 最近一次数值
     */
    private Double lastValue;

    /**
     * 最近一次数值的采集时间
     */
    private Date lastTime;

    public static MeasObjectVal fromMeasObj(MeasObject obj) {
        MeasObjectVal tmp = new MeasObjectVal();
        String str = JSONObject.toJSONString(obj);
        tmp = (MeasObjectVal) JSONObject.parseObject(str, MeasObjectVal.class);
        tmp.lastValue = 0.0;
        tmp.lastTime = new Date();
        return tmp;
    }

    public String toJson() {
        return JSONObject.toJSONString(this);
    }

    public static MeasObjectVal fromJson(String json) {
        return (MeasObjectVal) JSONObject.parseObject(json, MeasObjectVal.class);
    }
}
