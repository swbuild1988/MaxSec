package com.bandweaver.maxsec.controller;

import com.alibaba.fastjson.JSONObject;
import com.bandweaver.maxsec.dto.PageRequest;
import com.bandweaver.maxsec.dto.PageResult;
import com.bandweaver.maxsec.dto.R;
import com.bandweaver.maxsec.entity.Station;
import com.bandweaver.maxsec.entity.User;
import com.bandweaver.maxsec.service.ManagementService;
import com.bandweaver.maxsec.service.StationService;
import com.bandweaver.maxsec.service.UserService;
import com.bandweaver.maxsec.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;


@Slf4j
@RestController
public class StationController {

    @Autowired
    private StationService stationService;
    @Autowired
    private UserService userService;
    @Autowired
    private ManagementService managementService;

    @GetMapping("/stations")
    public R getStations(HttpServletRequest request) {

        // 获取当前用户所在的部门
        Integer userId = JwtUtil.getId(request);
        // 获取个人所属的场站
        List<Station> result = getPrivateStations(userId);

        return new R(result);
    }

    @GetMapping("/allstations")
    public R getAllStations() {
        return new R(stationService.selectAll());
    }


    @PostMapping("/stations_page")
    public R getPageInfo(HttpServletRequest request, @RequestBody JSONObject object) {
        PageResult result;
        PageRequest pageQuery = new PageRequest();
        pageQuery.setPageNum(object.getInteger("pageNum"));
        pageQuery.setPageSize(object.getInteger("pageSize"));

        result = stationService.selectPage(pageQuery);
        return new R(result);
    }

    @PostMapping("/stations")
    public R addStations(@RequestBody Station station) {
        station.setCreateTime(new Date());
        stationService.insert(station);
        return new R();
    }

    @PostMapping("/stations_edit")
    public R editStations(@RequestBody Station station) {
        stationService.update(station);
        return new R();
    }

    @GetMapping("/stations_delete/{id}")
    public R deleteStation(@PathVariable("id") Integer id) {
        stationService.delete(id);
        return new R();
    }

    @GetMapping("/stations/{id}")
    public R getStation(@PathVariable Integer id) {
        return new R(stationService.selectByPrimaryKey(id));
    }

    @GetMapping("/managements/{id}/stations")
    public R getStationsByManagement(@PathVariable Integer id) {
        return new R(stationService.getStationsByManagement(id));
    }

    /**
     * 获取个人所有的场站
     *
     * @param userId
     * @return
     */
    private List<Station> getPrivateStations(Integer userId) {
        User user = userService.selectByPrimaryKey(userId);
        Integer managementId = user.getManagementId();

        return stationService.getStationsByManagement(managementId);
    }

}
