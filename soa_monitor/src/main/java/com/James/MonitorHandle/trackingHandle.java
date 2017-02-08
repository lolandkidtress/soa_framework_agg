package com.James.MonitorHandle;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.James.basic.Model.trackingChain;
import com.James.basic.UtilsTools.CommonConfig;
import com.James.basic.UtilsTools.ThreadLocalCache;
import com.James.basic.UtilsTools.Utils;
import com.James.soa_agent.Agent_Advice_Method;
import com.James.soa_agent.event_handle.Agent_Handle;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Created by James on 16/7/21.
 * 记录调用链和耗时,写入kafka
 */
public class trackingHandle extends Agent_Handle {

  private final String classname = this.getClass().getName();

  @Override
  public Agent_Advice_Method attach_method(String class_name, Method method) {

    String method_name = method.getName();
    Agent_Advice_Method advice_method = new Agent_Advice_Method();
    advice_method.setMethod_name(method_name);
    advice_method.setLong_local_variable("attach_start_millis");

//    Class<?>[] parameterTypes = method.getParameterTypes();
//    StringBuilder stringbuilder = new StringBuilder();
//    for (Class<?> type : parameterTypes) {
//      stringbuilder.append(type.getName()).append(" ");
//    }
//    String full_method_name = stringbuilder.toString();
//    map.put(full_method_name, new AtomicInteger(1));

//    advice_method.setInsert_before("attach_start_millis =System.currentTimeMillis();com.James.demo.CodeInjection.My_Agent_Handle.insert_before_call_back( System.currentTimeMillis() );");
    advice_method.setInsert_before(
        "attach_start_millis =System.currentTimeMillis();"
            .concat(classname)
            .concat(".insert_before_call_back(\""+class_name+"\",\""+ method_name+"\",System.currentTimeMillis() );"));

    StringBuilder sbd = new StringBuilder();
//    sbd.append("javasist.sample.My_Agent_Handle.insert_after_call_back(");
    sbd.append(classname.concat(".insert_after_call_back("));
    sbd.append("attach_start_millis, System.currentTimeMillis());");
    advice_method.setInsert_after(sbd.toString());

    sbd.setLength(0);
//    sbd.append("javasist.sample.My_Agent_Handle.add_catch_call_back(");
    sbd.append(classname.concat(".add_catch_call_back("));
    sbd.append("$e);throw $e;");
    advice_method.setAdd_catch(sbd.toString());

    return advice_method;
  }

  private final Map<String, AtomicInteger> map = new HashMap<>();

  public static void insert_before_call_back(String class_name,String method_name,long start_millis) {

    trackingChain tc = ThreadLocalCache.getCallchain().get();
    if(tc==null){
      tc = new trackingChain(Utils.generateTrackingID());
    }
    tc.setToClass(class_name);
    tc.setToMethod(method_name);
    tc.setInvokerID(CommonConfig.clientID);
    tc.setStart_time(start_millis);
    ThreadLocalCache.setCallchain(tc);

  }

  public static void insert_after_call_back(long start_millis, long end_millis) {

    trackingChain tc = ThreadLocalCache.getCallchain().get();
    if(tc==null){
      tc = new trackingChain(Utils.generateTrackingID());
    }

    tc.setEnd_time(end_millis);
    tc.setStatus(true);
    ThreadLocalCache.setCallchain(tc);
    System.out.println("耗时:" + (end_millis - start_millis) + ",调用链:" + ThreadLocalCache.getCallchain().get().toJson());

  }

  public static void add_catch_call_back( Throwable e) {
    trackingChain tc = ThreadLocalCache.getCallchain().get();
    if(tc!=null){
      tc.setEnd_time(System.currentTimeMillis());
      tc.setStatus(false);
    }
    ThreadLocalCache.setCallchain(tc);
    System.out.println("异常:" + e.getCause());

  }

  public trackingChain doFilter(HttpServletRequest request, HttpServletResponse response)
      throws IOException,ServletException {
      System.out.println("webFilter call");
      return new trackingChain();
  }

}
