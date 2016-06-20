package com.James.demo.reflect;

import java.lang.reflect.Method;

/**
 * Created by James on 16/5/27.
 * 使用反射机制执行
 */
public class ReflectLaunch {

    final static String class_name = "com.James.demo.sample.LaunchTest";
    final static String method_name = "buildString";

    public void reflectLaunch(){
        try{
            Class clz = Class.forName(class_name);
            Object ob = clz.newInstance();
            Method[] methods = ob.getClass().getMethods();
            for(Method method : methods){
                if(method_name.equals(method.getName()) ){
                    System.out.println(method.getName());
                    method.invoke(ob, 2);
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        ReflectLaunch reflectLaunch = new ReflectLaunch();
        reflectLaunch.reflectLaunch();
    }
}
