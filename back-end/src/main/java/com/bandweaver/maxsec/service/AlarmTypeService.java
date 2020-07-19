package com.bandweaver.maxsec.service;

import com.bandweaver.maxsec.dto.PageRequest;
import com.bandweaver.maxsec.dto.PageResult;
import com.bandweaver.maxsec.entity.AlarmType;

import java.util.List;

public interface AlarmTypeService {
    AlarmType selectByPrimaryKey(Integer id);

    List<AlarmType> selectAll();

    PageResult selectPage(PageRequest pageRequest);

    void insert(AlarmType model);

    void update(AlarmType model);

    void delete(Integer id);
}
