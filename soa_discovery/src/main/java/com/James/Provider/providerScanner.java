package com.James.Provider;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.James.Annotation.InputParamAnnotation;
import com.James.Annotation.OutputParamAnnotation;
import com.James.Annotation.descriptionAnnotation;
import com.James.Model.InputParam;
import com.James.Model.OutputParam;
import com.James.Model.SharedProvider;
import com.James.basic.UtilsTools.CommonConfig;
import com.James.soa_agent.HotInjecter;


/**
 * Created by James on 16/5/30.
 * 服务扫描
 * 通过注解扫描
 */
public class providerScanner {

  private static final Logger LOGGER = LoggerFactory.getLogger(providerScanner.class.getName());

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
      Annotation[] inParams = method.getAnnotationsByType(InputParamAnnotation.class);
      Annotation[] outParams = method.getAnnotationsByType(OutputParamAnnotation.class);

      SharedProvider sharedProvider = new SharedProvider();
      //扫描描述
      sharedProvider = getDescribe(sharedProvider,method);

      //Inparam,Outparam
      for(InputParamAnnotation inParam: method.getAnnotationsByType(InputParamAnnotation.class)){
        InputParam inputParam = new InputParam();
        inputParam.setName(inParam.name());
        inputParam.setType(inParam.type());
        inputParam.setDescribe(inParam.describe());
        inputParam.setRequired(inParam.Required());
        inputParam.setDefault_value(inParam.default_value());

        sharedProvider.addInputParam(inputParam);

      }

      for(OutputParamAnnotation outParam: method.getAnnotationsByType(OutputParamAnnotation.class)){

        OutputParam outputParam = new OutputParam();
        outputParam.setName(outParam.name());
        outputParam.setType(outParam.type());
        outputParam.setDescribe(outParam.describe());
        outputParam.setRequired(outParam.Required());
        outputParam.setDefault_value(outParam.default_value());

        sharedProvider.addOutputParam(outputParam);
      }

      sharedProviders.add(sharedProvider);
    }

    return sharedProviders;

  }

  public static SharedProvider getDescribe(SharedProvider sharedProvider,Method method) {

    descriptionAnnotation describe = method.getAnnotation(descriptionAnnotation.class);
    InputParamAnnotation inParam = method.getAnnotation(InputParamAnnotation.class);
    OutputParamAnnotation outParam = method.getAnnotation(OutputParamAnnotation.class);

    if (describe != null) {
      LOGGER.info("开始扫描" + method.getDeclaringClass().getName() + "类下的" + method.getName() + "方法");
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
