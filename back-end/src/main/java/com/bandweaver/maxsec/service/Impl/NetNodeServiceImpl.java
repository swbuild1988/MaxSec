package com.bandweaver.maxsec.service.Impl;

import com.bandweaver.maxsec.entity.NetNode;
import com.bandweaver.maxsec.mapper.NetNodeMapper;
import com.bandweaver.maxsec.service.NetNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NetNodeServiceImpl implements NetNodeService {
    @Autowired
    private NetNodeMapper netNodeMapper;

    @Override
    public NetNode selectByPrimaryKey(Integer id) {
        return netNodeMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<NetNode> selectAll() {
        return netNodeMapper.selectAll();
    }

    @Override
    public void insert(NetNode model) {
        netNodeMapper.insert(model);
    }

    @Override
    public void update(NetNode model) {
        netNodeMapper.update(model);
    }

    @Override
    public void delete(Integer id) {
        netNodeMapper.delete(id);
    }
}
