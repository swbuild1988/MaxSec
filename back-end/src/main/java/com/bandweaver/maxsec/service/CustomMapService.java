package com.bandweaver.maxsec.service;

import com.bandweaver.maxsec.dto.PageRequest;
import com.bandweaver.maxsec.dto.PageResult;
import com.bandweaver.maxsec.entity.CustomMap;

import java.util.List;

public interface CustomMapService {
    CustomMap selectByPrimaryKey(Integer id);

    List<CustomMap> selectAll();

    PageResult selectPage(PageRequest request);

    void insert(CustomMap model);

    void update(CustomMap model);

    void delete(Integer id);
}
