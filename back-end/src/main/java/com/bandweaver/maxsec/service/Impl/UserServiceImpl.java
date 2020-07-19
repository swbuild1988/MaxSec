package com.bandweaver.maxsec.service.Impl;

import com.bandweaver.maxsec.entity.User;
import com.bandweaver.maxsec.mapper.UserMapper;
import com.bandweaver.maxsec.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User selectByPrimaryKey(Integer id) {
        return userMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<User> selectAll() {
        return userMapper.selectAll();
    }

    @Override
    public void insert(User user) {
        userMapper.insert(user);
    }

    @Override
    public void update(User user) {
        userMapper.update(user);
    }

    @Override
    public void delete(Integer id) {
        userMapper.delete(id);
    }

    @Override
    public User login(User user) {
        return userMapper.login(user);
    }

    @Override
    public User getUserByName(String name) {
        return userMapper.getUserByName(name);
    }
}
