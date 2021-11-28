package com.zqq.demo;

import com.spring.BeanPostProcessor;
import com.spring.Component;
import com.zqq.demo.controller.UserController;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

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
        if("userService".equals(beanName)){
            Object proxyInstance = Proxy.newProxyInstance(ZqqBeanPostProcessor.class.getClassLoader(), bean.getClass().getInterfaces(), new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    System.out.println("代理逻辑");  //找切点
                    return method.invoke(bean, args);
                }
            });

            return proxyInstance;
        }
        return bean;
    }
}
