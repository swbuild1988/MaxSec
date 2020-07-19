package com.bandweaver.maxsec.service;

import com.bandweaver.maxsec.dto.PageRequest;
import com.bandweaver.maxsec.dto.PageResult;
import com.bandweaver.maxsec.entity.Equipment;
import com.bandweaver.maxsec.vo.EquipmentVo;

import java.util.List;

public interface EquipmentService {
    Equipment selectByPrimaryKey(Integer id);

    List<Equipment> selectAll();

    void insert(Equipment model);

    void update(Equipment model);

    void delete(Integer id);

    /**
     * 根据条件获取设备数目
     * @param vo
     * @return
     */
    Integer getCountByCondition(EquipmentVo vo);

    /**
     * 根据条件分页获取设备数据
     *
     * @return
     */
    PageResult getPageListByCondition(PageRequest pageRequest, EquipmentVo vo);

}
