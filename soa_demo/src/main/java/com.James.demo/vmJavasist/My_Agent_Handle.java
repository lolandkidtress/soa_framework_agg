package com.James.demo.vmJavasist;

import com.James.soa_agent.Agent_Advice_Method;
import com.James.soa_agent.event_handle.Agent_Handle;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by James on 16/5/27.
 */
public class My_Agent_Handle extends Agent_Handle {

    @Override
    public Agent_Advice_Method attach_method(String class_name, Method method) {

        String method_name = method.getName();
        Agent_Advice_Method advice_method = new Agent_Advice_Method();
        advice_method.setMethod_name(method_name);
        advice_method.setLong_local_variable("attach_start_millis");

        Class<?>[] parameterTypes = method.getParameterTypes();
        StringBuilder stringbuilder = new StringBuilder();
        for (Class<?> type : parameterTypes) {
            stringbuilder.append(type.getName()).append(" ");
        }
        String full_method_name = stringbuilder.toString();
        map.put(full_method_name, new AtomicInteger(1));
        advice_method.setInsert_before("attach_start_millis =System.currentTimeMillis();javasist.sample.My_Agent_Handle.insert_before_call_back( System.currentTimeMillis() );");



        StringBuilder sbd = new StringBuilder();
        sbd.append("javasist.sample.My_Agent_Handle.insert_after_call_back(");
        sbd.append("attach_start_millis, System.currentTimeMillis());");
        advice_method.setInsert_after(sbd.toString());

        sbd.setLength(0);
        sbd.append("javasist.sample.My_Agent_Handle.add_catch_call_back(");
        sbd.append("$e);throw $e;");
        advice_method.setAdd_catch(sbd.toString());

        return advice_method;
    }

    private static final Map<String, AtomicInteger> map = new HashMap<>();

    public static void insert_before_call_back(long start_millis) {
        System.out.println("开始时间:"+ start_millis);
    }

    public static void insert_after_call_back(long start_millis, long end_millis) {
        System.out.println("耗时:"+ (end_millis - start_millis ));

    }

    public static void add_catch_call_back( Throwable e) {
        System.out.println("异常:" + e.getCause());
    }
}
