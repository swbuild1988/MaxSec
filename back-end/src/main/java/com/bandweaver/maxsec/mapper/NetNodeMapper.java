package com.bandweaver.maxsec.mapper;

import com.bandweaver.maxsec.entity.NetNode;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NetNodeMapper {
    NetNode selectByPrimaryKey(Integer id);

    List<NetNode> selectAll();

    void insert(NetNode model);

    void update(NetNode model);

    void delete(Integer id);
}
