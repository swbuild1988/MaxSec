package com.bandweaver.maxsec.service;

import com.bandweaver.maxsec.dto.PageRequest;
import com.bandweaver.maxsec.dto.PageResult;
import com.bandweaver.maxsec.entity.MeasObject;
import com.bandweaver.maxsec.vo.MeasObjectVo;

import java.util.List;

public interface MeasObjectService {
    MeasObject selectByPrimaryKey(Integer id);

    List<MeasObject> selectAll();

    void insert(MeasObject model);

    void update(MeasObject model);

    void delete(Integer id);

    List<MeasObject> selectByCondition(MeasObjectVo vo);

    /**
     * 根据条件分页获取对象
     *
     * @return
     */
    PageResult getPageListByCondition(PageRequest pageRequest, MeasObjectVo vo);
}
