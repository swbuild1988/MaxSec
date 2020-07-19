package com.bandweaver.maxsec.service.Impl;

import com.bandweaver.maxsec.dto.PageRequest;
import com.bandweaver.maxsec.dto.PageResult;
import com.bandweaver.maxsec.entity.Station;
import com.bandweaver.maxsec.entity.StationLink;
import com.bandweaver.maxsec.mapper.StationLinkMapper;
import com.bandweaver.maxsec.service.StationLinkService;
import com.bandweaver.maxsec.util.PageUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class StationLinkServiceImpl implements StationLinkService {
    @Autowired
    private StationLinkMapper stationLinkMapper;

    @Override
    public StationLink selectByPrimaryKey(Integer id) {
        return stationLinkMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<StationLink> selectAll() {
        return stationLinkMapper.selectAll();
    }

    @Override
    public PageResult selectPage(PageRequest request) {
        int pageNum = request.getPageNum();
        int pageSize = request.getPageSize();
        PageHelper.startPage(pageNum, pageSize);

        List<StationLink> sysMenus = stationLinkMapper.selectAll();
        PageInfo<StationLink> pageInfo = new PageInfo<StationLink>(sysMenus);

        return PageUtils.getPageResult(request, pageInfo);
    }

    @Override
    public void insert(StationLink model) {
        stationLinkMapper.insert(model);
    }

    @Override
    public void update(StationLink model) {
        stationLinkMapper.update(model);
    }

    @Override
    public void delete(Integer id) {
        stationLinkMapper.delete(id);
    }

}
