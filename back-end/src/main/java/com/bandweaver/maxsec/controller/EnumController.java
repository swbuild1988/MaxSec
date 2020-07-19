package com.bandweaver.maxsec.controller;

import com.alibaba.fastjson.JSONObject;
import com.bandweaver.maxsec.constants.*;
import com.bandweaver.maxsec.dto.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
public class EnumController {

    @GetMapping("/data-enum")
    public R getDataType() {
        List<JSONObject> list = new ArrayList<>();
        for (DataEnum item : DataEnum.values()) {
            JSONObject obj = new JSONObject();
            obj.put("key", item.getName());
            obj.put("val", item.getValue());
            list.add(obj);
        }
        return new R(list);
    }

    @GetMapping("/equipment-enum")
    public R getEquipmentType() {
        List<JSONObject> list = new ArrayList<>();
        for (EquipmentEnum item : EquipmentEnum.values()) {
            JSONObject obj = new JSONObject();
            obj.put("key", item.getName());
            obj.put("val", item.getValue());
            list.add(obj);
        }
        return new R(list);
    }

    @GetMapping("/equipment-state-enum")
    public R getEquipmentStateType() {
        List<JSONObject> list = new ArrayList<>();
        for (EquipmentStateEnum item : EquipmentStateEnum.values()) {
            JSONObject obj = new JSONObject();
            obj.put("key", item.getName());
            obj.put("val", item.getValue());
            list.add(obj);
        }
        return new R(list);
    }

    @GetMapping("/object-enum")
    public R getObjectType() {
        List<JSONObject> list = new ArrayList<>();
        for (ObjectEnum item : ObjectEnum.values()) {
            JSONObject obj = new JSONObject();
            obj.put("key", item.getName());
            obj.put("val", item.getValue());
            list.add(obj);
        }
        return new R(list);
    }

    @GetMapping("/alarm-level-enum")
    public R getAlarmLevel() {
        List<JSONObject> list = new ArrayList<>();
        for (AlarmLevelEnum item : AlarmLevelEnum.values()) {
            JSONObject obj = new JSONObject();
            obj.put("key", item.getName());
            obj.put("val", item.getValue());
            list.add(obj);
        }
        return new R(list);
    }
}
