package com.bandweaver.maxsec.controller;

import com.alibaba.fastjson.JSONObject;
import com.bandweaver.maxsec.dto.PageRequest;
import com.bandweaver.maxsec.dto.PageResult;
import com.bandweaver.maxsec.dto.R;
import com.bandweaver.maxsec.entity.MeasObject;
import com.bandweaver.maxsec.entity.MeasObjectVal;
import com.bandweaver.maxsec.service.MeasObjectService;
import com.bandweaver.maxsec.service.MeasObjectValService;
import com.bandweaver.maxsec.util.PageUtils;
import com.bandweaver.maxsec.vo.MeasObjectVo;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@RestController
public class MeasObjectValController {

    @Autowired
    private MeasObjectService measObjectService;
    @Autowired
    private MeasObjectValService measObjectValService;

    @PostMapping("/measobjectvals_edit")
    public R editMeasObjectVal(@RequestBody MeasObjectVal model) {
        measObjectValService.update(model);
        return new R();
    }

    @GetMapping("/measobjectvals_delete/{id}")
    public R deleteMeasObjectVal(@PathVariable("id") Integer id) {
        measObjectValService.delete(id);
        return new R();
    }

    @PostMapping("/measobjectvals/condition")
    public R getValsByCondition(@RequestBody JSONObject object) {
        MeasObjectVo vo = new MeasObjectVo();

        if (object.containsKey("stationId")) vo.setStationId(object.getInteger("stationId"));
        if (object.containsKey("dataType")) vo.setDataType(object.getInteger("dataType"));
        if (object.containsKey("objectType")) vo.setObjectType(object.getInteger("objectType"));

        if (!(object.containsKey("pageNum") && object.containsKey("pageSize"))) {

            // 如果不分页，直接从缓存中获取
            return new R(measObjectValService.selectByCondition(vo));

        } else {

            // 分页的话，先分页查数据库，在从缓存中获取数值
            PageRequest pageQuery = new PageRequest();
            pageQuery.setPageNum(object.getInteger("pageNum"));
            pageQuery.setPageSize(object.getInteger("pageSize"));

            PageResult measobjPageResult = measObjectService.getPageListByCondition(pageQuery, vo);
            List<MeasObjectVal> vals = new ArrayList<>();
            for (int i = 0; i < measobjPageResult.getContent().size(); i++) {
                MeasObject obj = (MeasObject)measobjPageResult.getContent().get(i);
                vals.add(measObjectValService.selectByPrimaryKey(obj.getId()));
            }
            PageInfo<MeasObjectVal> measObjectValPageInfo = new PageInfo<MeasObjectVal>(vals);
            return new R(PageUtils.getPageResult(pageQuery, measObjectValPageInfo));

        }
    }

}
