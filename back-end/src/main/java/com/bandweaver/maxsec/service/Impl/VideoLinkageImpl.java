package com.bandweaver.maxsec.service.Impl;

import com.bandweaver.maxsec.entity.VideoLinkage;
import com.bandweaver.maxsec.mapper.VideoLinkageMapper;
import com.bandweaver.maxsec.service.VideoLinkageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional
public class VideoLinkageImpl implements VideoLinkageService {
    @Autowired
    private VideoLinkageMapper videoLinkageMapper;

    // 缓存
    private ConcurrentHashMap<Integer, VideoLinkage> videoLinkageMap = new ConcurrentHashMap<>();

    @Override
    public VideoLinkage selectByPrimaryKey(Integer id) {
        if (!videoLinkageMap.containsKey(id)) {
            videoLinkageMap.put(id, videoLinkageMapper.selectByPrimaryKey(id));
        }
        return videoLinkageMap.get(id);
    }

    @Override
    public List<VideoLinkage> selectAll() {
        if (videoLinkageMap.size() == 0) {
            List<VideoLinkage> list = videoLinkageMapper.selectAll();
            for (VideoLinkage videoLinkage : list) {
                videoLinkageMap.put(videoLinkage.getId(), videoLinkage);
            }
        }
        return new ArrayList<>(videoLinkageMap.values());
    }

    @Override
    public void insert(VideoLinkage model) {
        videoLinkageMapper.insert(model);
        videoLinkageMap.put(model.getId(), model);
    }

    @Override
    public void update(VideoLinkage model) {
        videoLinkageMapper.update(model);
        videoLinkageMap.put(model.getId(), model);
    }

    @Override
    public void delete(Integer id) {
        videoLinkageMapper.delete(id);
        videoLinkageMap.remove(id);
    }
}
