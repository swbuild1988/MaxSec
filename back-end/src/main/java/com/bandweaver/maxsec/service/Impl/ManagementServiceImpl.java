package com.bandweaver.maxsec.service.Impl;

import com.bandweaver.maxsec.dto.PageRequest;
import com.bandweaver.maxsec.dto.PageResult;
import com.bandweaver.maxsec.dto.TreeManagement;
import com.bandweaver.maxsec.entity.Management;
import com.bandweaver.maxsec.entity.User;
import com.bandweaver.maxsec.mapper.ManagementMapper;
import com.bandweaver.maxsec.service.ManagementService;
import com.bandweaver.maxsec.service.UserService;
import com.bandweaver.maxsec.util.PageUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ManagementServiceImpl implements ManagementService {
    @Autowired
    private ManagementMapper managementMapper;
    @Autowired
    private UserService userService;

    @Override
    public Management selectByPrimaryKey(Integer id) {
        return managementMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Management> selectAll() {
        return managementMapper.selectAll();
    }

    @Override
    public PageResult selectPage(PageRequest request) {
        int pageNum = request.getPageNum();
        int pageSize = request.getPageSize();
        PageHelper.startPage(pageNum, pageSize);

        List<Management> sysMenus = managementMapper.selectAll();
        PageInfo<Management> pageInfo = new PageInfo<Management>(sysMenus);

        return PageUtils.getPageResult(request, pageInfo);
    }

    @Override
    public void insert(Management model) {
        managementMapper.insert(model);
    }

    @Override
    public void update(Management model) {
        managementMapper.update(model);
    }

    @Override
    public void delete(Integer id) {
        managementMapper.delete(id);
    }

    @Override
    public TreeManagement getTreeManagement(Integer id) {
        Management management = managementMapper.selectByPrimaryKey(id);
        List<Management> managements = managementMapper.selectAll();

        TreeManagement node = new TreeManagement(management);
        node = getNodeWithChildren(node, managements);

        return node;
    }

    @Override
    public TreeManagement getTreeManagementByUser(Integer userId) {
        User user = userService.selectByPrimaryKey(userId);
        Integer managementId = user.getManagementId();

        return getTreeManagement(managementId.intValue());
    }

    /**
     * 创建当前节点为根的管理部门树
     *
     * @param node        当前节点
     * @param managements 网络图的所有节点
     * @return
     */
    private TreeManagement getNodeWithChildren(TreeManagement node, List<Management> managements) {

        List<TreeManagement> children = node.getChildren();

        for (Management management : managements) {

            if (management.getFatherId().intValue() == node.getId()) {
                TreeManagement sonNode = new TreeManagement(management);
                sonNode = getNodeWithChildren(sonNode, managements);
                children.add(sonNode);
            }
        }

        node.setChildren(children);

        return node;
    }
}
