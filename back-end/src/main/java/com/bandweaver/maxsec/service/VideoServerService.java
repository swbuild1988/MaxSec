package com.bandweaver.maxsec.service;

import com.bandweaver.maxsec.dto.PageRequest;
import com.bandweaver.maxsec.dto.PageResult;
import com.bandweaver.maxsec.dto.VideoServerDto;
import com.bandweaver.maxsec.entity.VideoServer;

import java.util.List;

public interface VideoServerService {
    VideoServer selectByPrimaryKey(Integer id);

    VideoServerDto selectDtoByPrimaryKey(Integer id);

    List<VideoServer> selectAll();

    PageResult selectPage(PageRequest request);

    List<VideoServerDto> selectAllDto();

    void insert(VideoServer model);

    void update(VideoServer model);

    void delete(Integer id);
}
