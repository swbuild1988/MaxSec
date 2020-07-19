package com.bandweaver.maxsec.controller;

import com.alibaba.fastjson.JSONObject;
import com.bandweaver.maxsec.constants.EquipmentEnum;
import com.bandweaver.maxsec.constants.EquipmentStateEnum;
import com.bandweaver.maxsec.dto.PageRequest;
import com.bandweaver.maxsec.dto.PageResult;
import com.bandweaver.maxsec.dto.R;
import com.bandweaver.maxsec.dto.TreeManagement;
import com.bandweaver.maxsec.entity.Equipment;
import com.bandweaver.maxsec.entity.Station;
import com.bandweaver.maxsec.entity.User;
import com.bandweaver.maxsec.service.EquipmentService;
import com.bandweaver.maxsec.service.ManagementService;
import com.bandweaver.maxsec.service.StationService;
import com.bandweaver.maxsec.service.UserService;
import com.bandweaver.maxsec.util.JwtUtil;
import com.bandweaver.maxsec.vo.EquipmentVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Slf4j
@RestController
public class EquipmentController {

    @Autowired
    private EquipmentService equipmentService;
    @Autowired
    private UserService userService;
    @Autowired
    private ManagementService managementService;
    @Autowired
    private StationService stationService;

    @GetMapping("/equipments")
    public R getEquipments() {
        return new R(equipmentService.selectAll());
    }

    @PostMapping("/equipments")
    public R addEquipments(@RequestBody Equipment equipment) {
        equipment.setCreateTime(new Date());
        equipmentService.insert(equipment);
        return new R();
    }

    @PostMapping("/equipments_edit")
    public R editEquipments(@RequestBody Equipment equipment) {
        equipmentService.update(equipment);
        return new R();
    }

    @GetMapping("/equipments_delete/{id}")
    public R deleteEquipment(@PathVariable("id") Integer id) {
        equipmentService.delete(id);
        return new R();
    }

    @GetMapping("/equipments/{id}")
    public R getEquipment(@PathVariable Integer id) {
        return new R(equipmentService.selectByPrimaryKey(id));
    }

    @GetMapping("/equipment_type_statistic")
    public R getTypeStatistic(HttpServletRequest request) {

        List<JSONObject> result = new ArrayList<>();

        // 获取当前用户所在的部门
        Integer userId = JwtUtil.getId(request);
        User user = userService.selectByPrimaryKey(userId);
        Integer managementId = user.getManagementId();

        // 获取部门树
        TreeManagement tree = managementService.getTreeManagement(managementId.intValue());

        //
        if (tree.isLeaf()) {
            List<Station> stations = stationService.getStationsByManagement(tree.getId());

            for (Station station : stations) {
                JSONObject object = new JSONObject();
                // 添加场站名称
                object.put("product", station.getName());

                for (EquipmentEnum tmp : EquipmentEnum.values()) {
                    EquipmentVo vo = new EquipmentVo();
                    vo.setStationId(station.getId());
                    vo.setEquipmentType(tmp.getValue());
                    object.put(tmp.getName(), equipmentService.getCountByCondition(vo));
                }

                result.add(object);
            }

        } else {
            for (TreeManagement child : tree.getChildren()) {
                JSONObject object = new JSONObject();
                // 添加场站名称
                object.put("product", child.getName());

                List<Station> stations = stationService.getStationsByManagement(child.getId());
                for (EquipmentEnum tmp : EquipmentEnum.values()) {
                    int count = 0;
                    for (Station station : stations) {
                        EquipmentVo vo = new EquipmentVo();
                        vo.setStationId(station.getId());
                        vo.setEquipmentType(tmp.getValue());
                        count += equipmentService.getCountByCondition(vo);
                    }
                    object.put(tmp.getName(), count);
                }

                result.add(object);
            }
        }


        return new R(result);
    }

    @GetMapping("/equipment_type_state_statistic")
    public R getTypeStateStatistic(HttpServletRequest request) {

        List<JSONObject> result = new ArrayList<>();

        // 获取当前用户所在的部门
        Integer userId = JwtUtil.getId(request);
        User user = userService.selectByPrimaryKey(userId);
        Integer managementId = user.getManagementId();
        // 获取所有的场站
        List<Station> stations = stationService.getStationsByManagement(managementId);

        // 获取部门树
        TreeManagement tree = managementService.getTreeManagement(managementId.intValue());

        for (EquipmentEnum type : EquipmentEnum.values()) {
            JSONObject object1 = new JSONObject();
            object1.put("key", type.getName());

            List<JSONObject> l = new ArrayList<>();
            for (EquipmentStateEnum state : EquipmentStateEnum.values()) {
                JSONObject object2 = new JSONObject();

                int count = 0;
                for (Station station : stations) {
                    EquipmentVo vo = new EquipmentVo();
                    vo.setStationId(station.getId());
                    vo.setEquipmentState(state.getValue());
                    vo.setEquipmentType(type.getValue());
                    count += equipmentService.getCountByCondition(vo);
                }
                object2.put("key", state.getName());
                object2.put("val", count);
                l.add(object2);
            }

            object1.put("val", l);
            result.add(object1);
        }


        return new R(result);
    }

    /**
     * 获取部门下的所有设备（可能是具体某个场站）
     *
     * @param request
     * @param object
     * @return
     */
    @PostMapping("/equipments_page_management")
    public R getPageInfoByManagementName(HttpServletRequest request, @RequestBody JSONObject object) {

        PageRequest pageQuery = new PageRequest();
        String name;

        if (!(object.containsKey("name") && object.containsKey("pageNum") && object.containsKey("pageSize")))
            return new R(500, null, "");

        name = object.getString("name");
        pageQuery.setPageNum(object.getInteger("pageNum"));
        pageQuery.setPageSize(object.getInteger("pageSize"));


        PageResult result = null;

        // 获取当前用户所在的部门
        Integer userId = JwtUtil.getId(request);
        // 获取部门树
        TreeManagement tree = managementService.getTreeManagementByUser(userId);

        if (tree.isLeaf()) {        // 说明name查找的是场站
            // 获取所有的场站
            List<Station> stations = stationService.getStationsByManagement(tree.getId());

            for (Station station : stations) {
                if (station.getName().equals(name)) {
                    EquipmentVo vo = new EquipmentVo();
                    vo.setStationId(station.getId());
                    result = equipmentService.getPageListByCondition(pageQuery, vo);
                    break;
                }
            }
        } else {                    // 说明name查找的是下级部门
            for (TreeManagement management : tree.getChildren()) {
                if (management.getName().equals(name)) {
                    // 获取所有的场站
                    List<Station> stations = stationService.getStationsByManagement(management.getId());

                    if (stations.size() == 0) break;

                    List<Integer> idList = new ArrayList<>();
                    for (Station station : stations) {
                        idList.add(station.getId());
                    }

                    EquipmentVo vo = new EquipmentVo();
                    vo.setStationIds(idList);
                    result = equipmentService.getPageListByCondition(pageQuery, vo);

                    break;
                }
            }
        }

        return new R(result);
    }

    /**
     * 根据条件分页查询设备
     *
     * @param request
     * @param object
     * @return
     */
    @PostMapping("/equipments/condition")
    public R getPageInfoByCondition(HttpServletRequest request, @RequestBody JSONObject object) {
        PageResult result = null;

        EquipmentVo vo = new EquipmentVo();
        if (!(object.containsKey("pageNum") && object.containsKey("pageSize")))
            return new R(500, null, "");

        PageRequest pageQuery = new PageRequest();
        pageQuery.setPageNum(object.getInteger("pageNum"));
        pageQuery.setPageSize(object.getInteger("pageSize"));

        // 获取当前用户所在的部门
        Integer userId = JwtUtil.getId(request);
        User user = userService.selectByPrimaryKey(userId);
        Integer managementId = user.getManagementId();
        List<Station> stations = stationService.getStationsByManagement(managementId);
        List<Integer> idList = new ArrayList<>();
        for (Station station : stations) {
            idList.add(station.getId());
        }

        if (idList.size() > 0) vo.setStationIds(idList);
        if (object.containsKey("stationId")) vo.setStationId(object.getInteger("stationId"));
        if (object.containsKey("equipmentType")) vo.setEquipmentType(object.getInteger("equipmentType"));
        if (object.containsKey("equipmentState")) vo.setEquipmentState(object.getInteger("equipmentState"));

        log.info("vo:" + vo.toString());

        result = equipmentService.getPageListByCondition(pageQuery, vo);

        return new R(result);
    }

    /**
     * 获取某个场站下的各类型设备数
     *
     * @param id
     * @return
     */
    @GetMapping("/stations/{id}/equipment_state_statistic")
    public R getStateStatisticByStation(@PathVariable("id") Integer id) {
        List<JSONObject> result = new ArrayList<>();

        for (EquipmentStateEnum e : EquipmentStateEnum.values()) {

            JSONObject object = new JSONObject();
            object.put("key", e.getName());

            EquipmentVo vo = new EquipmentVo();
            vo.setStationId(id);
            vo.setEquipmentState(e.getValue());

            object.put("val", equipmentService.getCountByCondition(vo));

            result.add(object);
        }

        return new R(result);
    }
}
