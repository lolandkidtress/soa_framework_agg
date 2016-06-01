package com.James.Provider;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.LoggerFactory;

import com.James.Annotation.InputParamAnnotation;
import com.James.Annotation.OutputParamAnnotation;
import com.James.Annotation.descriptionAnnotation;
import com.James.Model.SharedProvider;
import com.James.basic.UtilsTools.CommonConfig;
import com.James.soa_agent.HotInjecter;


/**
 * Created by James on 16/5/30.
 * 服务扫描
 * 通过注解扫描
 */
public class providerScanner {

  private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(providerScanner.class.getName());

  //扫描所有的class
  //返回带有指定注解的类
  public static List<Class<?>> scanClass(Annotation annotationClass){
    List<Class<?>> classes = new ArrayList<>();
    Set<Class<?>> classset = HotInjecter.getInstance().getClasses();

    classset.forEach(clazz ->{
      Method[] methods = clazz.getMethods();
      for(Method method : methods){
        Annotation pAnnotation = method.getAnnotation(annotationClass.getClass());
        if(pAnnotation!=null){
          classes.add(clazz);
        }
      }
    });

    return classes;
  }

  //扫描所有的class
  //返回带有descriptionAnnotation的类
  public static Set<Class<?>> scanClasses(){
    Set<Class<?>> classes = new LinkedHashSet();
    Set<Class<?>> classset = HotInjecter.getInstance().getClasses();

    classset.forEach(clazz ->{
      Method[] methods = clazz.getMethods();
      for(Method method : methods){
        Annotation pAnnotation = method.getAnnotation(descriptionAnnotation.class);
        if(pAnnotation!=null){
          classes.add(clazz);
        }
      }
    });

    return classes;
  }

  //TODO 可以将RequestMapping传入
  public static Set<Class<?>> scanClasses(Class<? extends Annotation> annoClass){
    Set<Class<?>> classes = new LinkedHashSet();
    Set<Class<?>> classset = HotInjecter.getInstance().getClasses();

    classset.forEach(clazz ->{
      Method[] methods = clazz.getMethods();
      for(Method method : methods){
        Annotation pAnnotation = method.getAnnotation(annoClass);
        if(pAnnotation!=null){
          classes.add(clazz);
        }
      }
    });

    return classes;
  }


  //扫描指定的class
  //读取方法上的入参和出参
  public static List<SharedProvider> readClasses(Class<?> clazz){

    List<SharedProvider> sharedProviders =new ArrayList<>();

    Method[] methods = clazz.getMethods();
    for(Method method : methods){
      Annotation providerAnno = method.getAnnotation(descriptionAnnotation.class);
      Annotation inParam = method.getAnnotation(InputParamAnnotation.class);
      Annotation outParam = method.getAnnotation(OutputParamAnnotation.class);

      SharedProvider sharedProvider = new SharedProvider();
      //扫描方法上的注解
      sharedProvider = getDescribe(sharedProvider,method);

      //TODO Inparam,Outparam
      sharedProviders.add(sharedProvider);
    }

    return sharedProviders;

  }

  public static SharedProvider getDescribe(SharedProvider sharedProvider,Method method) {

    descriptionAnnotation describe = method.getAnnotation(descriptionAnnotation.class);
    InputParamAnnotation inParam = method.getAnnotation(InputParamAnnotation.class);
    OutputParamAnnotation outParam = method.getAnnotation(OutputParamAnnotation.class);

    if (describe != null) {
      LOGGER.info("开始扫描"+ method.getDeclaringClass().getName() +"类下的"+ method.getName()+"方法");
      sharedProvider.setAuthor(describe.author());
      sharedProvider.setMethod_name(describe.name());
      sharedProvider.setDescribe(describe.desc());
      sharedProvider.setVersion(describe.version());
      sharedProvider.setProtocol(describe.protocol());
      if(describe.protocol().equals(CommonConfig.PROTOCOL.http.name())){
        sharedProvider.setHttp_port(describe.port());
      }else{
        sharedProvider.setRpc_port(describe.port());
      }
      sharedProvider.setSubmit_mode(describe.submit_mode());

      if(!sharedProvider.isAvailable()){
        LOGGER.error(sharedProvider.getMethod_name()+"不可用");
      }
    }

    return sharedProvider;
  }
}
