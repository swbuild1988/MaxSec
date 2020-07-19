package com.bandweaver.maxsec.service.Impl;

import com.bandweaver.maxsec.constants.ObjectEnum;
import com.bandweaver.maxsec.dto.PageRequest;
import com.bandweaver.maxsec.dto.PageResult;
import com.bandweaver.maxsec.entity.MeasObject;
import com.bandweaver.maxsec.entity.MeasObjectVal;
import com.bandweaver.maxsec.entity.Video;
import com.bandweaver.maxsec.entity.VideoServer;
import com.bandweaver.maxsec.mapper.MeasObjectMapper;
import com.bandweaver.maxsec.mapper.MeasObjectValMapper;
import com.bandweaver.maxsec.mapper.VideoMapper;
import com.bandweaver.maxsec.mapper.VideoServerMapper;
import com.bandweaver.maxsec.service.MeasObjectService;
import com.bandweaver.maxsec.service.MeasObjectValService;
import com.bandweaver.maxsec.service.VideoServerService;
import com.bandweaver.maxsec.service.VideoService;
import com.bandweaver.maxsec.util.PageUtils;
import com.bandweaver.maxsec.vo.MeasObjectVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MeasObjectServiceImpl implements MeasObjectService {
    @Autowired
    private MeasObjectMapper measObjectMapper;
    @Autowired
    private VideoService videoService;
    @Autowired
    private VideoServerService videoServerService;
    @Autowired
    private MeasObjectValService measObjectValService;

    @Override
    public MeasObject selectByPrimaryKey(Integer id) {
        return measObjectMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<MeasObject> selectAll() {
        return measObjectMapper.selectAll();
    }

    @Override
    public void insert(MeasObject model) {
        switch (ObjectEnum.getEnum(model.getObjectType().intValue())) {
            case Methane:
            case Intrusion:
                measObjectValService.insert(MeasObjectVal.fromMeasObj(model));
                break;

            case Video:
            case HighVideo:
                videoService.insert(Video.fromMeasObj(model));
                break;

            case VideoServer:
                videoServerService.insert(VideoServer.fromMeasObj(model));
                break;
            case Intercom:

                break;
            default:
                measObjectMapper.insert(model);
                break;
        }
    }

    @Override
    public void update(MeasObject model) {
        measObjectMapper.update(model);
    }

    @Override
    public void delete(Integer id) {
        measObjectMapper.delete(id);
    }

    @Override
    public List<MeasObject> selectByCondition(MeasObjectVo vo) {
        return measObjectMapper.selectByCondition(vo);
    }

    @Override
    public PageResult getPageListByCondition(PageRequest pageRequest, MeasObjectVo vo) {
        int pageNum = pageRequest.getPageNum();
        int pageSize = pageRequest.getPageSize();
        PageHelper.startPage(pageNum, pageSize);

        List<MeasObject> sysMenus = measObjectMapper.selectByCondition(vo);
        PageInfo<MeasObject> measObjectPageInfo = new PageInfo<MeasObject>(sysMenus);

        return PageUtils.getPageResult(pageRequest, measObjectPageInfo);
    }
}
