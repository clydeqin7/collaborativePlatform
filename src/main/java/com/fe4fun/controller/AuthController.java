package com.fe4fun.controller;

import com.fe4fun.entity.Result;
import com.fe4fun.entity.User;
import com.fe4fun.service.UserService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import java.util.Map;

@Controller
public class AuthController {
    private UserService userService;
    private AuthenticationManager authenticationManager;

    @Inject
    public AuthController(AuthenticationManager authenticationManager, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }


    @GetMapping("/auth")
    @ResponseBody
    public Object auth() {
//        String username = SecurityContextHolder.getContext()
//                                               .getAuthentication()
//                                               .getName();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User loggedInUser = userService.getUserByUsername(authentication == null ? null : authentication.getName());
        if (loggedInUser == null) {
            return Result.failure("用户没有登录");
        }

        return Result.success("用户已登录", true, loggedInUser);
    }

    @GetMapping("/auth/logout")
    @ResponseBody
    public Object logout() {
        String username = SecurityContextHolder.getContext()
                                               .getAuthentication()
                                               .getName();
        User loggedInUser = userService.getUserByUsername(username);
        if (loggedInUser == null) {
            return Result.failure( "用户尚未登录");
        }
        SecurityContextHolder.clearContext();
        return Result.success("注销成功");
    }

    @PostMapping("/auth/login")
    @ResponseBody
    public Result login(@RequestBody Map<String, Object> usernameAndPassword) {
        String username = usernameAndPassword.get("username").toString();
        String password = usernameAndPassword.get("password").toString();

        UserDetails userDetails = null;
        try {
            userDetails = userService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            return Result.failure("用户名不存在");
        }

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, password);

        try {
            authenticationManager.authenticate(token);
            SecurityContextHolder.getContext()
                                 .setAuthentication(token);

            return Result.success("登录成功",true, userService.getUserByUsername(username));
        } catch (BadCredentialsException e) {
            return Result.failure("密码不正确");
        }

    }

    @PostMapping("/auth/register")
    @ResponseBody
    public Result register(@RequestBody Map<String, String> usernameAndPassword) {
        String username = usernameAndPassword.get("username");
        String password = usernameAndPassword.get("password");

        if (username == "" || password == "") {
            return Result.failure("用户名或密码不能为空");
        }
        if (username.length() > 15) {
            return Result.failure("invalid username");
        }
        if (password.length() > 15) {
            return Result.failure("invalid password");
        }


        try {
            userService.save(username, password);
        } catch (DuplicateKeyException e) {
            return Result.failure("user already exists");
        }

        return Result.success("注册成功", false, null);
    }

}
