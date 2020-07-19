package com.bandweaver.maxsec.mapper;

import com.bandweaver.maxsec.entity.Management;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ManagementMapper {
    Management selectByPrimaryKey(Integer id);

    List<Management> selectAll();

    void insert(Management model);

    void update(Management model);

    void delete(Integer id);
}
