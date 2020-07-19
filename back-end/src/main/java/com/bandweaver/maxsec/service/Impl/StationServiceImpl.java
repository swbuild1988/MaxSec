package com.bandweaver.maxsec.service.Impl;

import com.bandweaver.maxsec.dto.PageRequest;
import com.bandweaver.maxsec.dto.PageResult;
import com.bandweaver.maxsec.dto.TreeManagement;
import com.bandweaver.maxsec.entity.Station;
import com.bandweaver.maxsec.entity.User;
import com.bandweaver.maxsec.mapper.StationMapper;
import com.bandweaver.maxsec.mapper.UserMapper;
import com.bandweaver.maxsec.service.ManagementService;
import com.bandweaver.maxsec.service.StationService;
import com.bandweaver.maxsec.util.PageUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class StationServiceImpl implements StationService {
    @Autowired
    private StationMapper stationMapper;
    @Autowired
    private ManagementService managementService;

    @Override
    public Station selectByPrimaryKey(Integer id) {
        return stationMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Station> selectAll() {
        return stationMapper.selectAll();
    }

    @Override
    public PageResult selectPage(PageRequest request) {
        int pageNum = request.getPageNum();
        int pageSize = request.getPageSize();
        PageHelper.startPage(pageNum, pageSize);

        List<Station> sysMenus = stationMapper.selectAll();
        PageInfo<Station> pageInfo = new PageInfo<Station>(sysMenus);

        return PageUtils.getPageResult(request, pageInfo);
    }

    @Override
    public List<Station> getStationsByLeafManagement(Integer managementId) {
        return selectAll().stream().filter((Station station) -> station.getManagementId().intValue() == managementId.intValue())
                .collect(Collectors.toList());
    }

    @Override
    public List<Station> getStationsByManagement(Integer managementId) {

        // 获取部门树
        TreeManagement tree = managementService.getTreeManagement(managementId.intValue());

        // 深度搜索符合的场站
        List<Station> result = new ArrayList<>();
        List<Station> stations = selectAll();
        deepSearchManagement(result, stations, tree);

        return result;
    }


    /**
     * 深度搜索管理树
     *
     * @param result   最后的结果返回
     * @param stations 所有的场站原值
     * @param tree     树结构
     */
    private void deepSearchManagement(List<Station> result, List<Station> stations, TreeManagement node) {
        if (node.isLeaf()) {
            for (Station station : stations) {
                if (station.getManagementId().intValue() == node.getId()) {
                    result.add(station);
                }
            }
        } else {
            for (TreeManagement child : node.getChildren()) {
                deepSearchManagement(result, stations, child);
            }
        }
    }

    @Override
    public void insert(Station model) {
        stationMapper.insert(model);
    }

    @Override
    public void update(Station model) {
        stationMapper.update(model);
    }

    @Override
    public void delete(Integer id) {
        stationMapper.delete(id);
    }

}
