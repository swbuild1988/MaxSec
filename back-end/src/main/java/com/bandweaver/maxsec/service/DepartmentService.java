package com.bandweaver.maxsec.service;

import com.bandweaver.maxsec.entity.Department;

import java.util.List;

public interface DepartmentService {
    Department selectByPrimaryKey(Integer id);

    List<Department> selectAll();

    void insert(Department model);

    void update(Department model);

    void delete(Integer id);
}
