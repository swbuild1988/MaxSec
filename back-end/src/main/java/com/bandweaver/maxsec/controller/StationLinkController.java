package com.bandweaver.maxsec.controller;

import com.alibaba.fastjson.JSONObject;
import com.bandweaver.maxsec.dto.PageRequest;
import com.bandweaver.maxsec.dto.PageResult;
import com.bandweaver.maxsec.dto.R;
import com.bandweaver.maxsec.entity.StationLink;
import com.bandweaver.maxsec.service.StationLinkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@Slf4j
@RestController
public class StationLinkController {

    @Autowired
    private StationLinkService stationLinkService;

    @GetMapping("/stationlinks")
    public R getStationLinks() {
        return new R(stationLinkService.selectAll());
    }

    @PostMapping("/stationlinks")
    public R addStationLinks(@RequestBody StationLink stationLink) {
        stationLinkService.insert(stationLink);
        return new R();
    }

    @PostMapping("/stationlinks_page")
    public R getPageInfo(HttpServletRequest request, @RequestBody JSONObject object) {
        PageResult result;
        PageRequest pageQuery = new PageRequest();
        pageQuery.setPageNum(object.getInteger("pageNum"));
        pageQuery.setPageSize(object.getInteger("pageSize"));

        result = stationLinkService.selectPage(pageQuery);
        return new R(result);
    }


    @PostMapping("/stationlinks_edit")
    public R editStationLinks(@RequestBody StationLink stationLink) {
        stationLinkService.update(stationLink);
        return new R();
    }

    @GetMapping("/stationlinks_delete/{id}")
    public R deleteStationLink(@PathVariable("id") Integer id) {
        stationLinkService.delete(id);
        return new R();
    }

    @GetMapping("/stationlinks/{id}")
    public R getStationLink(@PathVariable Integer id) {
        return new R(stationLinkService.selectByPrimaryKey(id));
    }

}
