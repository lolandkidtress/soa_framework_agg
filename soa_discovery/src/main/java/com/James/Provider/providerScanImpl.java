package com.James.Provider;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.James.Filter.Annotation.degradeAnnotation;
import com.James.Filter.Annotation.ratelimitAnnotation;
import com.James.Filter.Filter;
import com.James.Filter.degrade.degradeCountDown;
import com.James.Filter.rateLimit.ratelimitCountDown;
import com.James.basic.Annotation.InputParamAnnotation;
import com.James.basic.Annotation.OutputParamAnnotation;
import com.James.basic.Annotation.descriptionAnnotation;
import com.James.basic.Enum.Code;
import com.James.basic.Model.InputParam;
import com.James.basic.Model.OutputParam;
import com.James.basic.Model.SharedNode;
import com.James.basic.UtilsTools.CommonConfig;
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
  //读取Filter信息
  public static List<SharedNode> readClasses(Class<?> clazz,String serverName){

    List<SharedNode> SharedNodes =new ArrayList<>();

    Method[] methods = clazz.getDeclaredMethods();
    for(Method method : methods){
      //避免接口实现重复读取
      if(!method.isBridge()){
        descriptionAnnotation descriptionAnno = method.getAnnotation(descriptionAnnotation.class);
        InputParamAnnotation[] inParams = method.getAnnotationsByType(InputParamAnnotation.class);
        OutputParamAnnotation[] outParams = method.getAnnotationsByType(OutputParamAnnotation.class);

        SharedNode SharedNode = new SharedNode(serverName);
        //读取desc信息
        SharedNode = getDescribe(SharedNode,method);

        //降级 filter
        degradeAnnotation DegradeAnnotation = method.getAnnotation(degradeAnnotation.class);
        if(DegradeAnnotation!=null){
          if(descriptionAnno!=null){

            int code = DegradeAnnotation.code() >0 ? DegradeAnnotation.code(): Code.service_degrade.code;
            String note = DegradeAnnotation.note().length()>0 ? DegradeAnnotation.note(): Code.service_degrade.note;

            degradeCountDown DegradeCountDown = new degradeCountDown(DegradeAnnotation.name(),
                DegradeAnnotation.allowPeriod(),
                DegradeAnnotation.allowTimes(),
                DegradeAnnotation.freezingTime(),
                code,
                note
            );

            Filter.getInstance().addDegradeConfig(DegradeCountDown);
            SharedNode.addDegradeFilter(DegradeCountDown.getName());
          }else{
            LOGGER.warn("检测到"+method.getName()+"配置了降级策略,但是没有配置description");
          }
        }

        //限流 filter
        ratelimitAnnotation RatelimitAnnotation = method.getAnnotation(ratelimitAnnotation.class);
        if(RatelimitAnnotation!=null){
          if(descriptionAnno!=null){

            int code = RatelimitAnnotation.code() >0 ? RatelimitAnnotation.code(): Code.over_limit.code;
            String note = RatelimitAnnotation.note().length()>0 ? RatelimitAnnotation.note(): Code.over_limit.note;

            ratelimitCountDown RatelimitCountDown = new ratelimitCountDown(RatelimitAnnotation.name(),
                RatelimitAnnotation.allowPeriod(),
                RatelimitAnnotation.allowTimes(),
                RatelimitAnnotation.freezingTime(),
                code,
                note
            );

            Filter.getInstance().addLimitConfig(RatelimitCountDown);
            SharedNode.addRatelimitFilter(RatelimitCountDown.getName());
          }else{
            LOGGER.warn("检测到"+method.getName()+"配置了限流策略,但是没有配置description");
          }
        }

        //Inparam,Outparam
        for(InputParamAnnotation inParam: inParams){
          InputParam InputParam = new InputParam();
          InputParam.setName(inParam.name());
          InputParam.setType(inParam.type());
          InputParam.setDescribe(inParam.describe());
          InputParam.setRequired(inParam.Required());
          InputParam.setDefault_value(inParam.default_value());

          SharedNode.addInputParam(InputParam);

        }

        for(OutputParamAnnotation outParam: outParams){
          OutputParam OutputParam = new OutputParam();
          OutputParam.setName(outParam.name());
          OutputParam.setType(outParam.type());
          OutputParam.setDescribe(outParam.describe());
          OutputParam.setRequired(outParam.Required());
          OutputParam.setDefault_value(outParam.default_value());

          SharedNode.addOutputParam(OutputParam);
        }

        SharedNodes.add(SharedNode);
      }

    }

    return SharedNodes;

  }

  //生成shareNode的描述
  public static SharedNode getDescribe(SharedNode SharedNode,Method method) {

    descriptionAnnotation describe = method.getAnnotation(descriptionAnnotation.class);

    if (describe != null) {
      LOGGER.info("开始扫描" + method.getDeclaringClass().getName() + "类下的" + method.getName() + "方法");
      SharedNode.setAuthor(describe.author());

      SharedNode.setDescribe(describe.desc());
      SharedNode.setVersion(describe.version());

      SharedNode.setProtocol(CommonConfig.PROTOCOL.valueOf(describe.protocol()));

      SharedNode.setSubmit_mode(describe.submit_mode());

      if(describe.protocol().equals(CommonConfig.PROTOCOL.avro.name())){
        SharedNode.setMethod_name(describe.name());
        //avro需要全限定名
        SharedNode.setDeclaringClass_name(method.getDeclaringClass().getName());
        SharedNode.setRpc_port(providerInstance.getInstance().getDefaultAvroPort());
      }

      if(describe.protocol().equals(CommonConfig.PROTOCOL.http.name())){
        SharedNode.setMethod_name(describe.name());
        SharedNode.setHttp_port(providerInstance.getInstance().getDefaultHttpPort());
        SharedNode.setHttp_context(providerInstance.getInstance().getDefaultHttpContext());
      }

      if(!SharedNode.isAvailable()) {
        LOGGER.error(SharedNode.getMethod_name()+"不可用");
      }
    }

    return SharedNode;
  }
}
