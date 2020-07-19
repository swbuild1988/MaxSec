package com.bandweaver.maxsec.controller;

import com.alibaba.fastjson.JSONObject;
import com.bandweaver.maxsec.dto.PageRequest;
import com.bandweaver.maxsec.dto.R;
import com.bandweaver.maxsec.entity.MeasObject;
import com.bandweaver.maxsec.service.MeasObjectService;
import com.bandweaver.maxsec.service.MeasObjectValService;
import com.bandweaver.maxsec.vo.MeasObjectVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;


@Slf4j
@RestController
public class MeasObjectController {

    @Autowired
    private MeasObjectService measObjectService;
    @Autowired
    private MeasObjectValService measObjectValService;

    @GetMapping("/measobjects")
    public R getAll() {
        return new R(measObjectService.selectAll());
    }

    @PostMapping("/measobjects")
    public R addStations(@RequestBody MeasObject model) {
        model.setCreateTime(new Date());
        measObjectService.insert(model);
        return new R();
    }

    @PostMapping("/measobjects_edit")
    public R editMeasObject(@RequestBody MeasObject model) {
        measObjectService.update(model);
        return new R();
    }

    @GetMapping("/measobjects_delete/{id}")
    public R deleteMeasObject(@PathVariable("id") Integer id) {
        measObjectService.delete(id);
        return new R();
    }

    @GetMapping("/measobjects/{id}")
    public R getModel(@PathVariable Integer id) {
        return new R(measObjectService.selectByPrimaryKey(id));
    }

    @PostMapping("/measobjects/condition")
    public R getInfoByCondition(@RequestBody JSONObject object) {
        MeasObjectVo vo = new MeasObjectVo();

        if (object.containsKey("stationId")) vo.setStationId(object.getInteger("stationId"));
        if (object.containsKey("dataType")) vo.setDataType(object.getInteger("dataType"));
        if (object.containsKey("objectType")) vo.setObjectType(object.getInteger("objectType"));

        if (!(object.containsKey("pageNum") && object.containsKey("pageSize"))) {    // 如果不分页
            return new R(measObjectService.selectByCondition(vo));
        } else {                                                                    // 分页
            PageRequest pageQuery = new PageRequest();
            pageQuery.setPageNum(object.getInteger("pageNum"));
            pageQuery.setPageSize(object.getInteger("pageSize"));
            return new R(measObjectService.getPageListByCondition(pageQuery, vo));
        }
    }


}
