package com.bandweaver.maxsec.service.Impl;

import com.bandweaver.maxsec.dto.VideoServerDto;
import com.bandweaver.maxsec.entity.VideoGroup;
import com.bandweaver.maxsec.entity.VideoServer;
import com.bandweaver.maxsec.mapper.MeasObjectMapper;
import com.bandweaver.maxsec.mapper.VideoGroupMapper;
import com.bandweaver.maxsec.mapper.VideoServerMapper;
import com.bandweaver.maxsec.service.VideoGroupService;
import com.bandweaver.maxsec.service.VideoServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class VideoGroupServiceImpl implements VideoGroupService {

    @Autowired
    private VideoGroupMapper videoGroupMapper;

    @Override
    public VideoGroup selectByPrimaryKey(Integer id) {
        return videoGroupMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<VideoGroup> selectAll() {
        return videoGroupMapper.selectAll();
    }

    @Override
    public List<VideoGroup> selectListByStation(Integer stationId) {
        return videoGroupMapper.selectListByStation(stationId);
    }

    @Override
    public void insert(VideoGroup model) {
        videoGroupMapper.insert(model);
    }

    @Override
    public void update(VideoGroup model) {
        videoGroupMapper.update(model);
    }

    @Override
    public void delete(Integer id) {
        videoGroupMapper.delete(id);
    }
}
