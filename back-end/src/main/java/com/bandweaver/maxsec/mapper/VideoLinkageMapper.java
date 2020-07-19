package com.bandweaver.maxsec.mapper;

import com.bandweaver.maxsec.entity.VideoLinkage;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface VideoLinkageMapper {
    VideoLinkage selectByPrimaryKey(Integer id);

    List<VideoLinkage> selectAll();

    void insert(VideoLinkage model);

    void update(VideoLinkage model);

    void delete(Integer id);
}
