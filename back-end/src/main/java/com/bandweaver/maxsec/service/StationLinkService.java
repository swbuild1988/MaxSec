package com.bandweaver.maxsec.service;

import com.bandweaver.maxsec.dto.PageRequest;
import com.bandweaver.maxsec.dto.PageResult;
import com.bandweaver.maxsec.entity.StationLink;

import java.util.List;

public interface StationLinkService {
    StationLink selectByPrimaryKey(Integer id);

    List<StationLink> selectAll();

    PageResult selectPage(PageRequest request);

    void insert(StationLink model);

    void update(StationLink model);

    void delete(Integer id);

}
