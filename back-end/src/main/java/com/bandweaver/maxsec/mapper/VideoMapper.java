package com.bandweaver.maxsec.mapper;

import com.bandweaver.maxsec.dto.VideoDto;
import com.bandweaver.maxsec.entity.Video;
import com.bandweaver.maxsec.vo.MeasObjectVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface VideoMapper {
    Video selectByPrimaryKey(Integer id);

    VideoDto selectDtoByPrimaryKey(Integer id);

    List<Video> selectAll();

    List<VideoDto> selectAllDto();

    List<VideoDto> selectVideoDtosByStation(Integer stationId);

    List<VideoDto> selectByCondition(MeasObjectVo vo);

    void insert(Video model);

    void update(Video model);

    void delete(Integer id);
}
