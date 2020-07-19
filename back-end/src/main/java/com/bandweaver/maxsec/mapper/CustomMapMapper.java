package com.bandweaver.maxsec.mapper;

import com.bandweaver.maxsec.entity.CustomMap;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CustomMapMapper {
    CustomMap selectByPrimaryKey(Integer id);

    List<CustomMap> selectAll();

    void insert(CustomMap model);

    void update(CustomMap model);

    void delete(Integer id);
}
