package com.bandweaver.maxsec.mapper;

import com.bandweaver.maxsec.entity.Station;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StationMapper {
    Station selectByPrimaryKey(Integer id);

    List<Station> selectAll();

    void insert(Station model);

    void update(Station model);

    void delete(Integer id);
}
