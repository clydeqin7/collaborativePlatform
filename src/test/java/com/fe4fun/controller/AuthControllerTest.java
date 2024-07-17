package com.fe4fun.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fe4fun.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpSession;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class AuthControllerTest {
    private MockMvc mvc;

    @Mock
    private UserService userService;
    @Mock
    private AuthenticationManager authenticationManager;
    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new AuthController(authenticationManager, userService)).build();
    }

    @Test
    void returnNotLoginByDefault() throws Exception {
        mvc.perform(get("/auth"))
           .andExpect(result -> Assertions.assertTrue(result.getResponse()
                                                            .getContentAsString(Charset.forName("UTF-8"))
                                                            .contains("用户没有登录")));
    }

    @Test
    void testLogin() throws Exception {
        // 未登录时，/auth接口返回未登录状态
        mvc.perform(get("/auth")).andExpect(status().isOk()).andExpect(result -> {
            Assertions.assertTrue(result.getResponse()
                                        .getContentAsString(Charset.forName("UTF-8"))
                                        .contains("用户没有登录"));
        });
        // 使用/auth/login登录
        Map<String, String> usernamePassword = new HashMap<>();
        usernamePassword.put("username", "MyUser");
        usernamePassword.put("password", "MyPassword");

        Mockito.when(userService.loadUserByUsername("MyUser"))
               .thenReturn(new User("MyUser", bCryptPasswordEncoder.encode("MyPassword"), Collections.emptyList()));
        Mockito.when(userService.getUserByUsername("MyUser"))
               .thenReturn(new com.fe4fun.entity.User(123, "MyUser", bCryptPasswordEncoder.encode("MyPassword")));

        MvcResult response = mvc.perform(post("/auth/login")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(new ObjectMapper().writeValueAsString(usernamePassword)))
                                .andExpect(status().isOk())
                                .andExpect(result -> {
                                    Assertions.assertTrue(result.getResponse()
                                                                .getContentAsString(Charset.forName("UTF-8"))
                                                                .contains("登录成功"));
                                })
                                .andReturn();

        HttpSession session = response.getRequest().getSession();

        // 再次检查/auth返回值，处于登录状态
        mvc.perform(get("/auth").session((MockHttpSession) session)).andExpect(status().isOk()).andExpect(result -> {
            Assertions.assertTrue(result.getResponse()
                                        .getContentAsString(Charset.forName("UTF-8"))
                                        .contains("用户已登录"));
        });
    }
}