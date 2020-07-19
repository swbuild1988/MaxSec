package com.bandweaver.maxsec.controller;

import com.alibaba.fastjson.JSONObject;
import com.bandweaver.maxsec.dto.PageRequest;
import com.bandweaver.maxsec.dto.PageResult;
import com.bandweaver.maxsec.dto.R;
import com.bandweaver.maxsec.entity.AlarmType;
import com.bandweaver.maxsec.service.AlarmTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@Slf4j
@RestController
public class AlarmTypeController {

    @Autowired
    private AlarmTypeService alarmTypeService;

    @GetMapping("/alarmtypes")
    public R getAlarmTypes() {
        return new R(alarmTypeService.selectAll());
    }

    @PostMapping("/alarmtypes")
    public R addAlarmTypes(@RequestBody AlarmType alarmType) {
        alarmTypeService.insert(alarmType);
        return new R();
    }

    @PostMapping("/alarmtypes_edit")
    public R editAlarmTypes(@RequestBody AlarmType alarmType) {
        alarmTypeService.update(alarmType);
        return new R();
    }

    @GetMapping("/alarmtypes_delete/{id}")
    public R deleteAlarmType(@PathVariable("id") Integer id) {
        alarmTypeService.delete(id);
        return new R();
    }

    @GetMapping("/alarmtypes/{id}")
    public R getAlarmType(@PathVariable Integer id) {
        return new R(alarmTypeService.selectByPrimaryKey(id));
    }

    @PostMapping("/alarmtypes_page")
    public R getPageInfo(HttpServletRequest request, @RequestBody JSONObject object) {
        PageResult result;
        PageRequest pageQuery = new PageRequest();
        pageQuery.setPageNum(object.getInteger("pageNum"));
        pageQuery.setPageSize(object.getInteger("pageSize"));

        result = alarmTypeService.selectPage(pageQuery);
        return  new R(result);
    }
}
