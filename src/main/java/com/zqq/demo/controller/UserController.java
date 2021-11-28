package com.zqq.demo.controller;


import com.zqq.demo.service.UserService;
import com.spring.Autowired;
import com.spring.Component;

@Component
public class UserController {

    @Autowired
    private UserService userService;
}
