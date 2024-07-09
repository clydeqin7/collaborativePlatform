package com.fe4fun.service;

import com.fe4fun.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Inject;

public class UserService {
    private UserMapper userMapper;

    @Inject
    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public User getUserById(Integer id) {
        return userMapper.findUserById(id);
    }
}
