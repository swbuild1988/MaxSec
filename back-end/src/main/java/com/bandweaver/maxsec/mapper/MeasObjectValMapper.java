package com.bandweaver.maxsec.mapper;

import com.bandweaver.maxsec.entity.MeasObjectVal;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MeasObjectValMapper {
    MeasObjectVal selectByPrimaryKey(Integer id);

    List<MeasObjectVal> selectAll();

    void insert(MeasObjectVal model);

    void update(MeasObjectVal model);

    void delete(Integer id);
}
