package com.bandweaver.maxsec.service.Impl;

import com.bandweaver.maxsec.dto.PageRequest;
import com.bandweaver.maxsec.dto.PageResult;
import com.bandweaver.maxsec.entity.AlarmType;
import com.bandweaver.maxsec.mapper.AlarmTypeMapper;
import com.bandweaver.maxsec.service.*;
import com.bandweaver.maxsec.util.PageUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional
public class AlarmTypeServiceImpl implements AlarmTypeService {
    @Autowired
    private AlarmTypeMapper alarmTypeMapper;

    // 缓存
    private ConcurrentHashMap<Integer, AlarmType> alarmTypeMap = new ConcurrentHashMap<>();

    @Override
    public AlarmType selectByPrimaryKey(Integer id) {
        if (!alarmTypeMap.containsKey(id)) {
            alarmTypeMap.put(id, alarmTypeMapper.selectByPrimaryKey(id));
        }
        return alarmTypeMap.get(id);
    }

    @Override
    public List<AlarmType> selectAll() {
        if (alarmTypeMap.size() == 0) {
            List<AlarmType> list = alarmTypeMapper.selectAll();
            for (AlarmType alarmType : list) {
                alarmTypeMap.put(alarmType.getId(), alarmType);
            }
        }
        return new ArrayList<>(alarmTypeMap.values());
    }

    @Override
    public PageResult selectPage(PageRequest pageRequest) {
        int pageNum = pageRequest.getPageNum();
        int pageSize = pageRequest.getPageSize();
        PageHelper.startPage(pageNum, pageSize);

        List<AlarmType> sysMenus = alarmTypeMapper.selectAll();
        PageInfo<AlarmType> equipmentDtoPageInfo = new PageInfo<AlarmType>(sysMenus);

        return PageUtils.getPageResult(pageRequest, equipmentDtoPageInfo);
    }

    @Override
    public void insert(AlarmType model) {
        alarmTypeMapper.insert(model);
        alarmTypeMap.put(model.getId(), model);
    }

    @Override
    public void update(AlarmType model) {
        alarmTypeMapper.update(model);
        alarmTypeMap.put(model.getId(), model);
    }

    @Override
    public void delete(Integer id) {
        alarmTypeMapper.delete(id);
        alarmTypeMap.remove(id);
    }
}
