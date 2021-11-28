package com.zqq.demo.service;

import com.spring.Component;

@Component("userService")
public class UserServiceImpl implements UserService {
    @Override
    public void test() {
        System.out.println("UserServiceImpl test()");
    }
}
