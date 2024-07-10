package com.fe4fun;

import com.fe4fun.entity.User;
import com.fe4fun.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    private UserService userService;

    public HelloController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/")
    public User index() {
        return this.userService.getUserById(1);
    }
}
