package com.bandweaver.maxsec.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bandweaver.maxsec.dto.R;
import com.bandweaver.maxsec.entity.Permission;
import com.bandweaver.maxsec.service.PermissionService;
import com.bandweaver.maxsec.service.UserService;
import com.bandweaver.maxsec.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
public class PermissionController {

    @Autowired
    private PermissionService permissionService;
    @Autowired
    private UserService userService;

    @GetMapping("/permissions")
    public R getList(HttpServletRequest request) {

        String authorization = request.getHeader("Authorization");
        Integer id = JwtUtil.getId(authorization);

        List<Permission> permissions = permissionService.getListByUser(id.intValue());
        return new R(permissions);
    }

    @GetMapping("/allpermissions")
    public R getAllList() {
        return new R(permissionService.getList());
    }

    @GetMapping("roles/{roleId}/permissions")
    public R getListByRole(@PathVariable int roleId) {
        List<Permission> permissions = permissionService.getListByRole(roleId);
        return new R(permissions);
    }

    @PostMapping("/permissions")
    public R addPermissions(@RequestBody List<Permission> permissions) {
        for (Permission permission : permissions) {
            permissionService.insert(permission);
        }
        return new R();
    }

    /**
     * 更新角色和权限的关系
     * 删除原来的所有角色关系
     * 添加现在的
     *
     * @param o
     * @return
     */
    @PostMapping("role-permission-relationship")
    public R editRolesAndPermissions(@RequestBody JSONObject o) {
        Integer roleId = o.getInteger("roleId");
        List<Integer> permissionIds = new ArrayList<>();
        JSONArray arr = o.getJSONArray("permissionIds");
        for (int i = 0; i < arr.size(); i++) {
            permissionIds.add(arr.getInteger(i));
        }

        permissionService.deleteAllPermissionByRole(roleId);
        for (Integer permissionId : permissionIds) {
            permissionService.addRelationOfRoleAndPermission(roleId, permissionId);
        }

        return new R();
    }

}
