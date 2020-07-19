package com.bandweaver.maxsec.service;

import com.bandweaver.maxsec.entity.Role;

import java.util.List;

public interface RoleService {
    Role selectByPrimaryKey(int id);
    List<Role> getRoles();
    List<Role> getRolesByUser(int userId);



    void insert(Role role);

    void update(Role role);

    void delete(Integer id);
}
