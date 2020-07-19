package com.bandweaver.maxsec.mapper;

import com.bandweaver.maxsec.entity.Department;
import com.bandweaver.maxsec.entity.Role;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DepartmentMapper {
    Department selectByPrimaryKey(Integer id);

    List<Department> selectAll();

    void insert(Department model);

    void update(Department model);

    void delete(Integer id);
}
