package com.bandweaver.maxsec.mapper;

import com.bandweaver.maxsec.dto.EquipmentDto;
import com.bandweaver.maxsec.entity.Equipment;
import com.bandweaver.maxsec.vo.EquipmentVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EquipmentMapper {
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
     * 根据条件分页获得设备
     * @return
     */
    List<EquipmentDto> getPageListByCondition(EquipmentVo vo);
}
