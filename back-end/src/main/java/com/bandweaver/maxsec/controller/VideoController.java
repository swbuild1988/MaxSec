package com.bandweaver.maxsec.controller;

import com.alibaba.fastjson.JSONObject;
import com.bandweaver.maxsec.dto.PageRequest;
import com.bandweaver.maxsec.dto.PageResult;
import com.bandweaver.maxsec.dto.R;
import com.bandweaver.maxsec.dto.VideoDto;
import com.bandweaver.maxsec.entity.Video;
import com.bandweaver.maxsec.service.VideoService;
import com.bandweaver.maxsec.vo.MeasObjectVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;


@Slf4j
@RestController
public class VideoController {

    @Autowired
    private VideoService videoService;

    @GetMapping("/videos")
    public R getAll() {
        return new R(videoService.selectAll());
    }

    @PostMapping("/videos_page")
    public R getPageInfo(HttpServletRequest request, @RequestBody JSONObject object) {
        PageResult result;
        PageRequest pageQuery = new PageRequest();
        pageQuery.setPageNum(object.getInteger("pageNum"));
        pageQuery.setPageSize(object.getInteger("pageSize"));

        result = videoService.selectPage(pageQuery);
        return new R(result);
    }

    @GetMapping("/videodtos")
    public R getAllDto() {
        List<VideoDto> videoDtos = videoService.selectAllDto();
        return new R(videoDtos);
    }

    @PostMapping("/videos")
    public R addVideos(@RequestBody Video model) {
        model.setCreateTime(new Date());
        videoService.insert(model);
        return new R();
    }

    @PostMapping("/videos_edit")
    public R editVideos(@RequestBody Video model) {
        videoService.update(model);
        return new R();
    }

    @GetMapping("/videos_delete/{id}")
    public R deleteVideo(@PathVariable("id") Integer id) {
        videoService.delete(id);
        return new R();
    }

    @GetMapping("/videos/{id}")
    public R getModel(@PathVariable Integer id) {
        return new R(videoService.selectByPrimaryKey(id));
    }

    @GetMapping("/videodtos/{id}")
    public R getDtoModel(@PathVariable Integer id) {
        return new R(videoService.selectDtoByPrimaryKey(id));
    }

    @GetMapping("/stations/{id}/videodtos")
    public R getVideoDtosByStation(@PathVariable("id") Integer id) {
        return new R(videoService.selectVideoDtosByStation(id));
    }

    @PostMapping("/videodtos/condition")
    public R getVideoDtosByCondition(@RequestBody JSONObject object) {
        MeasObjectVo vo = new MeasObjectVo();

        if (object.containsKey("stationId")) vo.setStationId(object.getInteger("stationId"));
        if (object.containsKey("objectType")) vo.setObjectType(object.getInteger("objectType"));

        List<VideoDto> videoDtos = videoService.selectByCondition(vo);

        return new R(videoDtos);
    }


}
