package com.zqq.demo;

import com.spring.ZqqApplicationContext;

public class Test {
    public static void main(String[] args) {
        ZqqApplicationContext zqqApplicationContext = new ZqqApplicationContext(AppConfig.class);
        Object userService = zqqApplicationContext.getBean("userService");
        Object userService2 = zqqApplicationContext.getBean("userService");
        Object userService3 = zqqApplicationContext.getBean("userService");
        System.out.println(userService);
        System.out.println(userService2);
        System.out.println(userService3);

    }
}
