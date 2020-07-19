package com.bandweaver.maxsec.controller;

import com.alibaba.fastjson.JSONObject;
import com.bandweaver.maxsec.dto.PageRequest;
import com.bandweaver.maxsec.dto.PageResult;
import com.bandweaver.maxsec.dto.R;
import com.bandweaver.maxsec.entity.VideoServer;
import com.bandweaver.maxsec.service.VideoServerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;


@Slf4j
@RestController
public class VideoServerController {

    @Autowired
    private VideoServerService videoServerService;

    @GetMapping("/videoservers")
    public R getAll() {
        return new R(videoServerService.selectAll());
    }

    @PostMapping("/videoservers_page")
    public R getPageInfo(HttpServletRequest request, @RequestBody JSONObject object) {
        PageResult result;
        PageRequest pageQuery = new PageRequest();
        pageQuery.setPageNum(object.getInteger("pageNum"));
        pageQuery.setPageSize(object.getInteger("pageSize"));

        result = videoServerService.selectPage(pageQuery);
        return new R(result);
    }

    @GetMapping("/videoserverdtos")
    public R getAllDto() {
        return new R(videoServerService.selectAllDto());
    }

    @PostMapping("/videoservers")
    public R addVideos(@RequestBody VideoServer model) {
        model.setCreateTime(new Date());
        videoServerService.insert(model);
        return new R();
    }

    @PostMapping("/videoservers_edit")
    public R editVideos(@RequestBody VideoServer model) {
        videoServerService.update(model);
        return new R();
    }

    @GetMapping("/videoservers_delete/{id}")
    public R deleteVideo(@PathVariable("id") Integer id) {
        videoServerService.delete(id);
        return new R();
    }

    @GetMapping("/videoservers/{id}")
    public R getModel(@PathVariable Integer id) {
        return new R(videoServerService.selectByPrimaryKey(id));
    }


}
