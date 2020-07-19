package com.bandweaver.maxsec.service.Impl;

import com.bandweaver.maxsec.dto.EquipmentDto;
import com.bandweaver.maxsec.dto.PageRequest;
import com.bandweaver.maxsec.dto.PageResult;
import com.bandweaver.maxsec.entity.Equipment;
import com.bandweaver.maxsec.mapper.EquipmentMapper;
import com.bandweaver.maxsec.service.EquipmentService;
import com.bandweaver.maxsec.util.PageUtils;
import com.bandweaver.maxsec.vo.EquipmentVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EquipmentServiceImpl implements EquipmentService {
    @Autowired
    private EquipmentMapper equipmentMapper;

    @Override
    public Equipment selectByPrimaryKey(Integer id) {
        return equipmentMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Equipment> selectAll() {
        return equipmentMapper.selectAll();
    }

    @Override
    public void insert(Equipment model) {
        equipmentMapper.insert(model);
    }

    @Override
    public void update(Equipment model) {
        equipmentMapper.update(model);
    }

    @Override
    public void delete(Integer id) {
        equipmentMapper.delete(id);
    }

    @Override
    public Integer getCountByCondition(EquipmentVo vo) {
        return equipmentMapper.getCountByCondition(vo);
    }

    @Override
    public PageResult getPageListByCondition(PageRequest pageRequest, EquipmentVo vo) {
        int pageNum = pageRequest.getPageNum();
        int pageSize = pageRequest.getPageSize();
        PageHelper.startPage(pageNum, pageSize);

        List<EquipmentDto> sysMenus = equipmentMapper.getPageListByCondition(vo);
        PageInfo<EquipmentDto> equipmentDtoPageInfo = new PageInfo<EquipmentDto>(sysMenus);

        return PageUtils.getPageResult(pageRequest, equipmentDtoPageInfo);
    }

}
