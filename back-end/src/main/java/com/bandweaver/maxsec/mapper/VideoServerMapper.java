package com.bandweaver.maxsec.mapper;

import com.bandweaver.maxsec.dto.VideoServerDto;
import com.bandweaver.maxsec.entity.VideoServer;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface VideoServerMapper {
    VideoServer selectByPrimaryKey(Integer id);

    VideoServerDto selectDtoByPrimaryKey(Integer id);

    List<VideoServer> selectAll();

    List<VideoServerDto> selectAllDto();

    void insert(VideoServer model);

    void update(VideoServer model);

    void delete(Integer id);
}
