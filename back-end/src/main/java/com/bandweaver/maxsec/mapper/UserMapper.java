package com.bandweaver.maxsec.mapper;

import com.bandweaver.maxsec.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    User selectByPrimaryKey(Integer id);

    List<User> selectAll();

    void insert(User user);

    void update(User user);

    void delete(Integer id);

    User login(User user);

    User getUserByName(String userName);
}
