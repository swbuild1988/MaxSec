package com.bandweaver.maxsec.service.Impl;

import com.bandweaver.maxsec.constants.Constants;
import com.bandweaver.maxsec.dto.PageRequest;
import com.bandweaver.maxsec.dto.PageResult;
import com.bandweaver.maxsec.dto.VideoDto;
import com.bandweaver.maxsec.entity.Video;
import com.bandweaver.maxsec.mapper.MeasObjectMapper;
import com.bandweaver.maxsec.mapper.VideoMapper;
import com.bandweaver.maxsec.service.VideoService;
import com.bandweaver.maxsec.util.PageUtils;
import com.bandweaver.maxsec.util.RedisUtil;
import com.bandweaver.maxsec.util.TypeConvertUtil;
import com.bandweaver.maxsec.vo.MeasObjectVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@Transactional
public class VideoServiceImpl implements VideoService {
    @Autowired
    private VideoMapper videoMapper;
    @Autowired
    private MeasObjectMapper measObjectMapper;

    // 缓存
    private ConcurrentHashMap<Integer, VideoDto> videoDtoMap = new ConcurrentHashMap<>();

    @Override
    public Video selectByPrimaryKey(Integer id) {
        return videoMapper.selectByPrimaryKey(id);
    }

    @Override
    public VideoDto selectDtoByPrimaryKey(Integer id) {
        if (!videoDtoMap.containsKey(id)) {
            videoDtoMap.put(id, videoMapper.selectDtoByPrimaryKey(id));
        }
        return videoDtoMap.get(id);
    }

    @Override
    public List<Video> selectAll() {
        return videoMapper.selectAll();
    }

    @Override
    public PageResult selectPage(PageRequest request) {
        int pageNum = request.getPageNum();
        int pageSize = request.getPageSize();
        PageHelper.startPage(pageNum, pageSize);

        List<Video> sysMenus = videoMapper.selectAll();
        PageInfo<Video> pageInfo = new PageInfo<Video>(sysMenus);

        return PageUtils.getPageResult(request, pageInfo);
    }

    @Override
    public List<VideoDto> selectAllDto() {
        if (videoDtoMap.size() == 0) {
            List<VideoDto> list = videoMapper.selectAllDto();
            for (VideoDto videoLinkage : list) {
                videoDtoMap.put(videoLinkage.getId(), videoLinkage);
            }
        }
        return new ArrayList<>(videoDtoMap.values());
    }

    @Override
    public List<VideoDto> selectVideoDtosByStation(Integer stationId) {
        return videoMapper.selectVideoDtosByStation(stationId);
    }

    @Override
    public List<VideoDto> selectByCondition(MeasObjectVo vo) {
        return videoMapper.selectByCondition(vo);
    }

    @Override
    public void insert(Video model) {
        measObjectMapper.insert(model);
        videoMapper.insert(model);
        videoDtoMap.put(model.getId(), selectDtoByPrimaryKey(model.getId()));
    }

    @Override
    public void update(Video model) {
        measObjectMapper.update(model);
        videoMapper.update(model);
        videoDtoMap.put(model.getId(), selectDtoByPrimaryKey(model.getId()));
    }

    @Override
    public void delete(Integer id) {
        videoMapper.delete(id);
        measObjectMapper.delete(id);
        videoDtoMap.remove(id);
    }
}
