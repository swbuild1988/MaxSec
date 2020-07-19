package com.bandweaver.maxsec.service.Impl;

import com.bandweaver.maxsec.dto.PageRequest;
import com.bandweaver.maxsec.dto.PageResult;
import com.bandweaver.maxsec.entity.CustomMap;
import com.bandweaver.maxsec.mapper.CustomMapMapper;
import com.bandweaver.maxsec.service.CustomMapService;
import com.bandweaver.maxsec.util.PageUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CustomMapServiceImpl implements CustomMapService {
    @Autowired
    private CustomMapMapper geoMapMapper;

    @Override
    public CustomMap selectByPrimaryKey(Integer id) {
        return geoMapMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<CustomMap> selectAll() {
        return geoMapMapper.selectAll();
    }

    @Override
    public PageResult selectPage(PageRequest request) {
        int pageNum = request.getPageNum();
        int pageSize = request.getPageSize();
        PageHelper.startPage(pageNum, pageSize);

        List<CustomMap> sysMenus = geoMapMapper.selectAll();
        PageInfo<CustomMap> pageInfo = new PageInfo<CustomMap>(sysMenus);

        return PageUtils.getPageResult(request, pageInfo);
    }

    @Override
    public void insert(CustomMap model) {
        geoMapMapper.insert(model);
    }

    @Override
    public void update(CustomMap model) {
        geoMapMapper.update(model);
    }

    @Override
    public void delete(Integer id) {
        geoMapMapper.delete(id);
    }
}
