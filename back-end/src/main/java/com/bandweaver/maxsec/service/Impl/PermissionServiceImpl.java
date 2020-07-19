package com.bandweaver.maxsec.service.Impl;

import com.bandweaver.maxsec.entity.Permission;
import com.bandweaver.maxsec.mapper.PermissionMapper;
import com.bandweaver.maxsec.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public List<Permission> getList() {
        return permissionMapper.getList();
    }

    @Override
    public List<Permission> getListByRole(int roleId) {
        return permissionMapper.getListByRole(roleId);
    }

    @Override
    public List<Permission> getListByUser(int userId) {
        return permissionMapper.getListByUser(userId);
    }

    @Override
    public void insert(Permission permission) {
        permissionMapper.insert(permission);
    }

    @Override
    public void update(Permission permission) {
        permissionMapper.update(permission);
    }

    @Override
    public void delete(Integer id) {
        permissionMapper.delete(id);
    }

    @Override
    public void deleteAllPermissionByRole(Integer roleId) {
        permissionMapper.deleteAllPermissionByRole(roleId);
    }

    @Override
    public void addRelationOfRoleAndPermission(Integer roleId, Integer permissionId) {
        permissionMapper.addRelationOfRoleAndPermission(roleId, permissionId);
    }
}
