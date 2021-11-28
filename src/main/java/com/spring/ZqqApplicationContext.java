package com.spring;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
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

        String componentScanVaule = componentScanAnnotation.value();
        System.out.println("注解上获取到的扫描包名 componentScanVaule = " + componentScanVaule);

        if(componentScanAnnotation == null){
            throw new RuntimeException("扫描的路径不能为空,请检查注解的value值");
        }

        //扫描
        //BootstrapClassloader   加载  jre/lib
        //ExtClassloader         加载  jre/ext/lib
        //AppClassloader         加载  classpath
        ClassLoader classLoader = ZqqApplicationContext.class.getClassLoader();  //AppClassloader
        URL resource = classLoader.getResource(componentScanVaule.replace(".", "/"));
        File file = new File(resource.getFile());

        scanFile(componentScanVaule, classLoader, file);
    }

    private void scanFile(String componentScanVaule, ClassLoader classLoader, File file)  {
//        File file = new File(resource.getFile());
        System.out.println("当前扫描的包名 = " + componentScanVaule);
        String componentScanVaule2 = componentScanVaule;
        if(file.exists() && file.isDirectory()){
            File[] files = file.listFiles();
            for(File f: files){
                String filePath = f.getAbsolutePath();
                if(f.exists() && f.isDirectory()){

                    System.out.println("当前文件的具体路径 = " + f.getAbsolutePath());
//                    System.out.println(filePath.lastIndexOf(componentScanVaule.replace(".", "\\")));

                    String newPackage = filePath.substring(filePath.lastIndexOf(componentScanVaule.replace(".", "\\")), filePath.length());
                    componentScanVaule2 = newPackage.replace("\\", ".");
                    System.out.println("新组成的包名是: " + componentScanVaule2);
                    scanFile(componentScanVaule2, classLoader, f);
                }

                processFile(componentScanVaule, classLoader, filePath);
            }
        }else{
            processFile(componentScanVaule2, classLoader, file.getAbsolutePath());
        }

    }

    private void processFile(String componentScanVaule, ClassLoader classLoader, String filePath) {
        if(!filePath.endsWith(".class")){
            return;
        }
        System.out.println("componentScanVaule包名下找到的class文件  -->  " + filePath);
        String fileName = filePath.substring(filePath.lastIndexOf("\\") + 1, filePath.lastIndexOf("."));
        String newPath = componentScanVaule + "." + fileName;
        System.out.println("转换之后的完整的类名 -->  " + newPath);
        try {
            Class<?> clazz = classLoader.loadClass(newPath);
            if(!clazz.isAnnotationPresent(Component.class)){
                return;
            }

            System.out.println("找到的带有Component注解的class文件" + filePath);
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
            Class clazz = beanDefinition.getClazz();

            Constructor declaredConstructor = clazz.getDeclaredConstructor();

            Field[] declaredFields = clazz.getDeclaredFields();
            for(Field field: declaredFields){
                if(field.isAnnotationPresent(Autowired.class)){
                    System.out.println("有Autowired注解的filed = " + field);
                }
            }

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
