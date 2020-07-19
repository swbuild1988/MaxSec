package com.bandweaver.maxsec.service.Impl;

import com.bandweaver.maxsec.entity.Department;
import com.bandweaver.maxsec.mapper.DepartmentMapper;
import com.bandweaver.maxsec.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional
public class DepartmentServiceImpl implements DepartmentService {
    @Autowired
    private DepartmentMapper departmentMapper;

    @Override
    public Department selectByPrimaryKey(Integer id) {
        return departmentMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Department> selectAll() {
        return departmentMapper.selectAll();
    }

    @Override
    public void insert(Department model) {
        departmentMapper.insert(model);
    }

    @Override
    public void update(Department model) {
        departmentMapper.update(model);
    }

    @Override
    public void delete(Integer id) {
        departmentMapper.delete(id);
    }
}
