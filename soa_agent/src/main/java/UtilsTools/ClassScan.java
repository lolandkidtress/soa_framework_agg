package UtilsTools;

import java.lang.annotation.Annotation;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.James.soa_agent.HotInjecter;
import com.James.soa_agent.event_handle.ScanAnnotationClass_Handle;


/**
 * Created by James on 16/9/28.
 */
public class ClassScan {

  private static final Logger LOGGER = LogManager.getLogger(ClassScan.class.getName());

  //扫描method上有指定注解的class
  public static Object scanAnnotationClasses(Class<? extends Annotation> annotationClass ,Class<? extends ScanAnnotationClass_Handle> scanHandle) {

    Set<Class<?>> classset = HotInjecter.getInstance().getClasses();
    try{
    ScanAnnotationClass_Handle newInstance = scanHandle.newInstance();
    return newInstance.attach_annotation(classset,annotationClass);

    }catch(Exception e){
      LOGGER.error("#实例化ScanAnnotationClass_Handle失败:", e);
      return null;
    }

  }
}
