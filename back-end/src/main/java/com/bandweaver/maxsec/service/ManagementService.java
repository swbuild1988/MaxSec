package com.bandweaver.maxsec.service;

import com.bandweaver.maxsec.dto.PageRequest;
import com.bandweaver.maxsec.dto.PageResult;
import com.bandweaver.maxsec.dto.TreeManagement;
import com.bandweaver.maxsec.entity.Management;

import java.util.List;

public interface ManagementService {
    Management selectByPrimaryKey(Integer id);

    List<Management> selectAll();

    PageResult selectPage(PageRequest request);

    void insert(Management model);

    void update(Management model);

    void delete(Integer id);

    /**
     * 获取当前部门的树结构
     * @param id
     * @return
     */
    TreeManagement getTreeManagement(Integer id);


    /**
     * 获取用户所有的部门的树结构
     * @param userId
     * @return
     */
    TreeManagement getTreeManagementByUser(Integer userId);
}
