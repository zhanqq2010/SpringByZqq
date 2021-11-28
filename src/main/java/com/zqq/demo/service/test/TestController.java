package com.zqq.demo.service.test;


import com.spring.Autowired;
import com.spring.Component;
import com.zqq.demo.service.UserService;
//测试在子包下能否被扫描到
@Component
public class TestController {

    @Autowired
    private UserService userService;
}
