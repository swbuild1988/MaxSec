package com.bandweaver.maxsec.service;

import com.bandweaver.maxsec.entity.Permission;

import java.util.List;

public interface PermissionService {
    List<Permission> getList();
    List<Permission> getListByRole(int roleId);
    List<Permission> getListByUser(int userId);


    void insert(Permission permission);

    void update(Permission permission);

    void delete(Integer id);

    /**
     * 删除某角色的所有权限
     * @param roleId
     */
    void deleteAllPermissionByRole(Integer roleId);

    /**
     * 添加角色和权限的关系
     * @param roleId
     * @param permissionId
     */
    void addRelationOfRoleAndPermission(Integer roleId, Integer permissionId);
}
