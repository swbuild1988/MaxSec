package com.bandweaver.maxsec.mapper;

import com.bandweaver.maxsec.entity.StationLink;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StationLinkMapper {
    StationLink selectByPrimaryKey(Integer id);

    List<StationLink> selectAll();

    void insert(StationLink model);

    void update(StationLink model);

    void delete(Integer id);
}
