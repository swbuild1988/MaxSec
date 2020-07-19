package com.bandweaver.maxsec.mapper;

import com.bandweaver.maxsec.entity.AlarmType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AlarmTypeMapper {
    AlarmType selectByPrimaryKey(Integer id);

    List<AlarmType> selectAll();

    void insert(AlarmType model);

    void update(AlarmType model);

    void delete(Integer id);
}
