package com.bandweaver.maxsec.service;

import com.bandweaver.maxsec.entity.NetNode;

import java.util.List;

public interface NetNodeService {
    NetNode selectByPrimaryKey(Integer id);

    List<NetNode> selectAll();

    void insert(NetNode model);

    void update(NetNode model);

    void delete(Integer id);
}
