package com.bandweaver.maxsec.mapper;

import com.bandweaver.maxsec.entity.MeasObject;
import com.bandweaver.maxsec.vo.MeasObjectVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MeasObjectMapper {
    MeasObject selectByPrimaryKey(Integer id);

    List<MeasObject> selectAll();

    void insert(MeasObject model);

    void update(MeasObject model);

    void delete(Integer id);

    List<MeasObject> selectByCondition(MeasObjectVo vo);
}
