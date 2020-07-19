package com.bandweaver.maxsec.service.Impl;

import com.bandweaver.maxsec.dto.PageRequest;
import com.bandweaver.maxsec.dto.PageResult;
import com.bandweaver.maxsec.dto.VideoServerDto;
import com.bandweaver.maxsec.entity.Video;
import com.bandweaver.maxsec.entity.VideoServer;
import com.bandweaver.maxsec.mapper.MeasObjectMapper;
import com.bandweaver.maxsec.mapper.VideoServerMapper;
import com.bandweaver.maxsec.service.VideoServerService;
import com.bandweaver.maxsec.util.PageUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional
public class VideoServerServiceImpl implements VideoServerService {
    @Autowired
    private VideoServerMapper videoServerMapper;
    @Autowired
    private MeasObjectMapper measObjectMapper;

    // 缓存
    private ConcurrentHashMap<Integer, VideoServerDto> videoServerDtoMap = new ConcurrentHashMap<>();

    @Override
    public VideoServer selectByPrimaryKey(Integer id) {
        return videoServerMapper.selectByPrimaryKey(id);
    }

    @Override
    public VideoServerDto selectDtoByPrimaryKey(Integer id) {
        if (!videoServerDtoMap.containsKey(id)) {
            videoServerDtoMap.put(id, videoServerMapper.selectDtoByPrimaryKey(id));
        }
        return videoServerDtoMap.get(id);
    }

    @Override
    public List<VideoServer> selectAll() {
        return videoServerMapper.selectAll();
    }

    @Override
    public PageResult selectPage(PageRequest request) {
        int pageNum = request.getPageNum();
        int pageSize = request.getPageSize();
        PageHelper.startPage(pageNum, pageSize);

        List<VideoServer> sysMenus = videoServerMapper.selectAll();
        PageInfo<VideoServer> pageInfo = new PageInfo<VideoServer>(sysMenus);

        return PageUtils.getPageResult(request, pageInfo);
    }

    @Override
    public List<VideoServerDto> selectAllDto() {
        if (videoServerDtoMap.size() == 0) {
            List<VideoServerDto> list = videoServerMapper.selectAllDto();
            for (VideoServerDto videoLinkage : list) {
                videoServerDtoMap.put(videoLinkage.getId(), videoLinkage);
            }
        }
        return new ArrayList<>(videoServerDtoMap.values());
    }

    @Override
    public void insert(VideoServer model) {
        measObjectMapper.insert(model);
        videoServerMapper.insert(model);
        videoServerDtoMap.put(model.getId(), selectDtoByPrimaryKey(model.getId()));
    }

    @Override
    public void update(VideoServer model) {
        measObjectMapper.update(model);
        videoServerMapper.update(model);
        videoServerDtoMap.put(model.getId(), selectDtoByPrimaryKey(model.getId()));
    }

    @Override
    public void delete(Integer id) {
        videoServerMapper.delete(id);
        measObjectMapper.delete(id);
        videoServerDtoMap.remove(id);
    }
}
