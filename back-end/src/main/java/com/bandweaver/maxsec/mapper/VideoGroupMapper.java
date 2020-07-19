package com.bandweaver.maxsec.mapper;

import com.bandweaver.maxsec.entity.VideoGroup;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface VideoGroupMapper {

    VideoGroup selectByPrimaryKey(Integer id);

    List<VideoGroup> selectAll();

    List<VideoGroup> selectListByStation(Integer stationId);

    void insert(VideoGroup model);

    void update(VideoGroup model);

    void delete(Integer id);
}
