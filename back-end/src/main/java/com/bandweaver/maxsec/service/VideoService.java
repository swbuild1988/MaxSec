package com.bandweaver.maxsec.service;

import com.bandweaver.maxsec.dto.PageRequest;
import com.bandweaver.maxsec.dto.PageResult;
import com.bandweaver.maxsec.dto.VideoDto;
import com.bandweaver.maxsec.entity.Video;
import com.bandweaver.maxsec.vo.MeasObjectVo;

import java.util.List;

public interface VideoService {
    Video selectByPrimaryKey(Integer id);

    VideoDto selectDtoByPrimaryKey(Integer id);

    List<Video> selectAll();

    PageResult selectPage(PageRequest request);

    List<VideoDto> selectAllDto();

    List<VideoDto> selectVideoDtosByStation(Integer stationId);

    List<VideoDto> selectByCondition(MeasObjectVo vo);

    void insert(Video model);

    void update(Video model);

    void delete(Integer id);
}
