package com.bandweaver.maxsec.mapper;

import com.bandweaver.maxsec.entity.Schedule;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ScheduleMapper {
    Schedule selectByPrimaryKey(Integer id);

    List<Schedule> selectAll();

    void insert(Schedule model);

    void update(Schedule model);

    void delete(Integer id);
}
