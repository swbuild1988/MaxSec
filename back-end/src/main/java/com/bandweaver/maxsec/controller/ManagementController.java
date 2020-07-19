package com.bandweaver.maxsec.controller;

import com.alibaba.fastjson.JSONObject;
import com.bandweaver.maxsec.dto.PageRequest;
import com.bandweaver.maxsec.dto.PageResult;
import com.bandweaver.maxsec.dto.R;
import com.bandweaver.maxsec.dto.TreeManagement;
import com.bandweaver.maxsec.entity.Management;
import com.bandweaver.maxsec.entity.User;
import com.bandweaver.maxsec.service.ManagementService;
import com.bandweaver.maxsec.service.UserService;
import com.bandweaver.maxsec.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;


@Slf4j
@RestController
public class ManagementController {

    @Autowired
    private ManagementService managementService;
    @Autowired
    private UserService userService;


    @GetMapping("/managements/tree")
    public R getManagementsTree(HttpServletRequest request) {

        // 获取当前用户所在的部门
        Integer userId = JwtUtil.getId(request);
        User user = userService.selectByPrimaryKey(userId);
        Integer managementId = user.getManagementId();

        // 获取部门树
        TreeManagement tree = managementService.getTreeManagement(managementId.intValue());

        return new R(tree);
    }


    @GetMapping("/managements")
    public R getManagements() {
        return new R(managementService.selectAll());
    }

    @PostMapping("/managements_page")
    public R getPageInfo(HttpServletRequest request, @RequestBody JSONObject object) {
        PageResult result;
        PageRequest pageQuery = new PageRequest();
        pageQuery.setPageNum(object.getInteger("pageNum"));
        pageQuery.setPageSize(object.getInteger("pageSize"));

        result = managementService.selectPage(pageQuery);
        return new R(result);
    }

    @PostMapping("/managements")
    public R addManagements(@RequestBody Management management) {
        management.setCreateTime(new Date());
        managementService.insert(management);
        return new R();
    }

    @PostMapping("/managements_edit")
    public R editManagements(@RequestBody Management management) {
        managementService.update(management);
        return new R();
    }

    @GetMapping("/managements_delete/{id}")
    public R deleteManagement(@PathVariable("id") Integer id) {
        managementService.delete(id);
        return new R();
    }

    @GetMapping("/managements/{id}")
    public R getManagement(@PathVariable Integer id) {
        return new R(managementService.selectByPrimaryKey(id));
    }




}
