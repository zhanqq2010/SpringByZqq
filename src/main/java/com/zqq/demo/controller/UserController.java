package com.zqq.demo.controller;


import com.spring.BeanNameAware;
import com.spring.InitializingBean;
import com.zqq.demo.service.UserService;
import com.spring.Autowired;
import com.spring.Component;

@Component("userController")
public class UserController implements BeanNameAware, InitializingBean {

    @Autowired
    private UserService userService;

    private String beanName;
    private String name;

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }


    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("初始化");
    }


    public void test() {
        System.out.println(userService);
        System.out.println(beanName);
        System.out.println(name);
    }


}
