package com.bandweaver.maxsec.service;

import com.bandweaver.maxsec.entity.Schedule;

import java.util.List;

public interface ScheduleService {
    Schedule selectByPrimaryKey(Integer id);

    List<Schedule> selectAll();

    void insert(Schedule model);

    void update(Schedule model);

    void delete(Integer id);
}
