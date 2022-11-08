package com.example.rgwaimai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.rgwaimai.entity.User;
import com.example.rgwaimai.mapper.UserMapper;
import com.example.rgwaimai.service.UserService;
import org.springframework.stereotype.Service;


/**
 * @authro zl
 * @create 2022-11-07-23:31
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}