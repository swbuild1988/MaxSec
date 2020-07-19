package com.bandweaver.maxsec.service;

import com.bandweaver.maxsec.dto.PageRequest;
import com.bandweaver.maxsec.dto.PageResult;
import com.bandweaver.maxsec.entity.Station;

import java.util.List;

public interface StationService {
    Station selectByPrimaryKey(Integer id);

    List<Station> selectAll();

    PageResult selectPage(PageRequest request);

    List<Station> getStationsByLeafManagement(Integer managementId);

    List<Station> getStationsByManagement(Integer managementId);

    void insert(Station model);

    void update(Station model);

    void delete(Integer id);

}
