package com.bandweaver.maxsec.service;

import com.bandweaver.maxsec.entity.VideoGroup;

import java.util.List;

public interface VideoGroupService {
    VideoGroup selectByPrimaryKey(Integer id);

    List<VideoGroup> selectAll();

    List<VideoGroup> selectListByStation(Integer stationId);

    void insert(VideoGroup model);

    void update(VideoGroup model);

    void delete(Integer id);
}
