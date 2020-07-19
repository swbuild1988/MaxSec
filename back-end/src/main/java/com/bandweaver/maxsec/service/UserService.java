package com.bandweaver.maxsec.service;

import com.bandweaver.maxsec.entity.User;

import java.util.List;

public interface UserService {
    User selectByPrimaryKey(Integer id);

    List<User> selectAll();

    void insert(User user);

    void update(User user);

    void delete(Integer id);

    User login(User user);

    User getUserByName(String name);
}
