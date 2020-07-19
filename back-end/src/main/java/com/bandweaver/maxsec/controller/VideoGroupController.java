package com.bandweaver.maxsec.controller;

import com.bandweaver.maxsec.dto.R;
import com.bandweaver.maxsec.entity.VideoGroup;
import com.bandweaver.maxsec.service.VideoGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;


@Slf4j
@RestController
public class VideoGroupController {

    @Autowired
    private VideoGroupService videoGroupService;

    @GetMapping("/videogroups")
    public R getAll() {
        return new R(videoGroupService.selectAll());
    }

    @PostMapping("/videogroups")
    public R addVideoGroups(@RequestBody VideoGroup model) {
        model.setCreateTime(new Date());
        videoGroupService.insert(model);
        return new R();
    }

    @PostMapping("/videogroups_edit")
    public R editVideoGroups(@RequestBody VideoGroup model) {
        videoGroupService.update(model);
        return new R();
    }

    @GetMapping("/videogroups_delete/{id}")
    public R deleteVideoGroup(@PathVariable("id") Integer id) {
        videoGroupService.delete(id);
        return new R();
    }

    @GetMapping("/stations/{id}/videogroups")
    public R getVideoGroups(@PathVariable Integer id) {
        return new R(videoGroupService.selectListByStation(id));
    }


}
