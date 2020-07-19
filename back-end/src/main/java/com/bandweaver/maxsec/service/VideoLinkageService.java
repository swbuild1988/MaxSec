package com.bandweaver.maxsec.service;

import com.bandweaver.maxsec.entity.VideoLinkage;

import java.util.List;

public interface VideoLinkageService {
    VideoLinkage selectByPrimaryKey(Integer id);

    List<VideoLinkage> selectAll();

    void insert(VideoLinkage model);

    void update(VideoLinkage model);

    void delete(Integer id);
}
