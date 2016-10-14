package com.James.Provider;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.James.basic.Annotation.InputParamAnnotation;
import com.James.basic.Annotation.OutputParamAnnotation;
import com.James.basic.Annotation.descriptionAnnotation;
import com.James.basic.Annotation.mockAnnotation;
import com.James.basic.Model.inputParam;
import com.James.basic.Model.mockPolicy;
import com.James.basic.Model.outputParam;
import com.James.basic.Model.sharedNode;
import com.James.basic.UtilsTools.CommonConfig;
import com.James.basic.UtilsTools.Return;
import com.James.soa_agent.HotInjecter;
import com.James.soa_agent.event_handle.ScanAnnotationClass_Handle;


/**
 * Created by James on 16/9/28.
 * 服务扫描
 * 通过注解扫描
 */
public class providerScanImpl implements ScanAnnotationClass_Handle {
  private static final Log LOGGER = LogFactory.getLog(providerScanImpl.class.getName());

  @Override
  public Object attach_annotation(Set<Class<?>> Classes,Class<? extends Annotation> annotationClass) {

    Set<Class<?>> classes = new LinkedHashSet();
    Set<Class<?>> classset = HotInjecter.getInstance().getClasses();

    classset.forEach(clazz ->{
      Method[] methods = clazz.getMethods();
      for(Method method : methods){
        Annotation pAnnotation = method.getAnnotation(annotationClass);
        if(pAnnotation!=null){
          classes.add(clazz);
        }
      }
    });

    return classes;
  }


  //扫描指定的class
  //读取desc信息
  //读取方法上的入参和出参
  public static List<sharedNode> readClasses(Class<?> clazz,String serverName){

    List<sharedNode> sharedNodes =new ArrayList<>();

    Method[] methods = clazz.getMethods();
    for(Method method : methods){
//      Annotation providerAnno = method.getAnnotation(descriptionAnnotation.class);
//      Annotation[] inParams = method.getAnnotationsByType(InputParamAnnotation.class);
//      Annotation[] outParams = method.getAnnotationsByType(OutputParamAnnotation.class);

      sharedNode sharedNode = new sharedNode(serverName);
      //读取desc信息
      sharedNode = getDescribe(sharedNode,method);

      //降级 policy
      mockAnnotation mockAnno = method.getAnnotation(mockAnnotation.class);
      if (mockAnno != null) {
        descriptionAnnotation desAnnotation = method.getAnnotation(descriptionAnnotation.class);
        if(desAnnotation!=null) {
          mockPolicy MockPolicy = new mockPolicy();
          MockPolicy.setName(sharedNode.getDescribe().concat(mockAnno.name()));
          MockPolicy.setPolicy(mockAnno.policy());
          MockPolicy.setAllowFailPeriod(mockAnno.allowFailPeriod());
          MockPolicy.setAllowFailTimes(mockAnno.allowFailTimes());
          MockPolicy.setFreezingTime(mockAnno.freezingTime());
          Return mockReturn = Return.FAIL(mockAnno.code(),mockAnno.note());
          MockPolicy.setMockReturn(mockReturn);

          sharedNode.setMockPolicy(MockPolicy);
        }else{
          LOGGER.warn("检测到"+method.getName()+"配置了降级策略,但是没有配置description");
        }
      }

      //Inparam,Outparam
      for(InputParamAnnotation inParam: method.getAnnotationsByType(InputParamAnnotation.class)){
        inputParam InputParam = new inputParam();
        InputParam.setName(inParam.name());
        InputParam.setType(inParam.type());
        InputParam.setDescribe(inParam.describe());
        InputParam.setRequired(inParam.Required());
        InputParam.setDefault_value(inParam.default_value());

        sharedNode.addInputParam(InputParam);

      }

      for(OutputParamAnnotation outParam: method.getAnnotationsByType(OutputParamAnnotation.class)){

        outputParam OutputParam = new outputParam();
        OutputParam.setName(outParam.name());
        OutputParam.setType(outParam.type());
        OutputParam.setDescribe(outParam.describe());
        OutputParam.setRequired(outParam.Required());
        OutputParam.setDefault_value(outParam.default_value());

        sharedNode.addOutputParam(OutputParam);
      }

      sharedNodes.add(sharedNode);
    }

    return sharedNodes;

  }

  //生成shareNode的描述
  public static sharedNode getDescribe(sharedNode sharedNode,Method method) {

    descriptionAnnotation describe = method.getAnnotation(descriptionAnnotation.class);

    if (describe != null) {
      LOGGER.info("开始扫描" + method.getDeclaringClass().getName() + "类下的" + method.getName() + "方法");
      sharedNode.setAuthor(describe.author());

      sharedNode.setDescribe(describe.desc());
      sharedNode.setVersion(describe.version());

      sharedNode.setProtocol(CommonConfig.PROTOCOL.valueOf(describe.protocol()));

      sharedNode.setSubmit_mode(describe.submit_mode());

      if(describe.protocol().equals(CommonConfig.PROTOCOL.avro.name())){
        sharedNode.setMethod_name(describe.name());
        //avro需要全限定名
        sharedNode.setDeclaringClass_name(method.getDeclaringClass().getName());
        sharedNode.setRpc_port(providerInstance.getInstance().getDefaultAvroPort());
      }

      if(describe.protocol().equals(CommonConfig.PROTOCOL.http.name())){
        sharedNode.setMethod_name(describe.name());
        sharedNode.setHttp_port(providerInstance.getInstance().getDefaultHttpPort());
        sharedNode.setHttp_context(providerInstance.getInstance().getDefaultHttpContext());
      }

      if(!sharedNode.isAvailable()) {
        LOGGER.error(sharedNode.getMethod_name()+"不可用");
      }
    }

    return sharedNode;
  }
}
