package com.bandweaver.maxsec.mapper;

import com.bandweaver.maxsec.entity.Role;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoleMapper {
    Role selectByPrimaryKey(int id);
    List<Role> getRoles();
    List<Role> getRolesByUser(int userId);



    void insert(Role role);

    void update(Role role);

    void delete(Integer id);
}
