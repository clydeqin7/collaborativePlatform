package com.fe4fun.configuration;

import com.fe4fun.mapper.UserMapper;
import com.fe4fun.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JavaConfiguration {
    @Bean
    public UserService userService(UserMapper userMapper) {
        return new UserService(userMapper);
    }
}
