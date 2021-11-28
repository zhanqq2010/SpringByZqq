package com.zqq.demo;

import com.spring.ZqqApplicationContext;
import com.zqq.demo.controller.UserController;


public class Test {
    public static void main(String[] args) {
        ZqqApplicationContext zqqApplicationContext = new ZqqApplicationContext(AppConfig.class);
        UserController userController = (UserController) zqqApplicationContext.getBean("userController");
//        Object userService2 = zqqApplicationContext.getBean("userService");
//        Object userService3 = zqqApplicationContext.getBean("userService");
        System.out.println(userController);
        userController.test();

    }
}
