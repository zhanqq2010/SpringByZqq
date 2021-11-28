package com.zqq.spring;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ZqqApplicationContext {

    private Class configClass;
    private Map<String, Object> singletonObjects = new ConcurrentHashMap<>();
    private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    public ZqqApplicationContext(Class configClass) {
        this.configClass = configClass;
        //解析配置
        //获取到是否有ComponentScan注解 -> 扫描路径 -> 扫描 ->beanDefinition -> beanDefinitionMap
        scan(configClass);

        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()){
            Object bean = createBean(entry.getValue());
            singletonObjects.put(entry.getKey(), bean);
        }

    }

    private void scan(Class configClass) {
        if(!configClass.isAnnotationPresent(ComponentScan.class)){
            throw  new RuntimeException("配置文件类需要加上ComponentScan注解");
        }
        ComponentScan componentScanAnnotation = (ComponentScan) configClass.getDeclaredAnnotation(ComponentScan.class);
        if(componentScanAnnotation == null){
            throw new RuntimeException("配置文件类需要加上ComponentScan注解");
        }

        String path = componentScanAnnotation.value();
        System.out.println("path = " + path);

        if(componentScanAnnotation == null){
            throw new RuntimeException("扫描的路径不能为空,请检查注解的value值");
        }

        //扫描
        //BootstrapClassloader   加载  jre/lib
        //ExtClassloader         加载  jre/ext/lib
        //AppClassloader         加载  classpath
        ClassLoader classLoader = ZqqApplicationContext.class.getClassLoader();  //AppClassloader
        URL resource = classLoader.getResource(path.replace(".", "/"));
        File file = new File(resource.getFile());
        File[] files = file.listFiles();
        for(File f: files){
            String filePath = f.getAbsolutePath();
            if(!filePath.endsWith(".class")){
                continue;
            }
            System.out.println(filePath);
            String fileName = filePath.substring(filePath.lastIndexOf("\\") + 1, filePath.lastIndexOf("."));
            String newPath = path + "." + fileName;
            System.out.println(newPath);
            try {
                Class<?> clazz = classLoader.loadClass(newPath);
                if(!clazz.isAnnotationPresent(Component.class)){
                    continue;
                }
                Component component = clazz.getDeclaredAnnotation(Component.class);
                String beanName = component.value();
                if(Utils.isEmpty(beanName)){
                    beanName = Utils.toLowerCaseFirstOne(fileName);
                }


                //解析类，判断当前bean是单例bean还是prototype类型bean
                //BeanConfinition
                BeanDefinition beanDefinition = new BeanDefinition();
                beanDefinition.setClazz(clazz);
                beanDefinition.setBeanName(beanName);
                if(clazz.isAnnotationPresent(Scope.class)){
                    //有Scope注解
                    Scope scope = clazz.getDeclaredAnnotation(Scope.class);

                    beanDefinition.setScope(scope.value());
                }else{
                    //没有Scope注解，代表的是单例
                    beanDefinition.setScope("singleton");


                }
                beanDefinitionMap.put(beanName, beanDefinition);



            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public Object getBean(String beanName){
        Object bean = null;
        if(beanDefinitionMap.containsKey(beanName)){
            BeanDefinition beanDefinition = (BeanDefinition) beanDefinitionMap.get(beanName);
            if("singleton".equals(beanDefinition.getScope())){
                bean = singletonObjects.get(beanName);
            }else{
                bean = createBean(beanDefinition);
            }
        }else {
            //不存在这个bean
            throw  new RuntimeException("没有定义此bean");
        }
        return bean;
    }

    private Object createBean(BeanDefinition beanDefinition) {
        try {
            Constructor declaredConstructor = beanDefinition.getClazz().getDeclaredConstructor();
            Object o = declaredConstructor.newInstance();
            return o;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();

        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }
}
