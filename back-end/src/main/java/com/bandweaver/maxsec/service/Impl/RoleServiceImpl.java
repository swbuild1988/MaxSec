package com.bandweaver.maxsec.service.Impl;

import com.bandweaver.maxsec.entity.Role;
import com.bandweaver.maxsec.mapper.RoleMapper;
import com.bandweaver.maxsec.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public Role selectByPrimaryKey(int id) {
        return roleMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Role> getRoles() {
        return roleMapper.getRoles();
    }

    @Override
    public List<Role> getRolesByUser(int userId) {
        return roleMapper.getRolesByUser(userId);
    }

    @Override
    public void insert(Role role) {
        roleMapper.insert(role);
    }

    @Override
    public void update(Role role) {
        roleMapper.update(role);
    }

    @Override
    public void delete(Integer id) {
        roleMapper.delete(id);
    }
}
