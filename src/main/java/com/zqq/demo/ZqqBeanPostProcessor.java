package com.zqq.demo;

import com.spring.BeanPostProcessor;
import com.spring.Component;
import com.zqq.demo.controller.UserController;

@Component
public class ZqqBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        if("userController".equals(beanName)){
            System.out.println("初始化前");
            ((UserController)bean).setName("zqq");
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        System.out.println("初始化后");
        return bean;
    }
}
