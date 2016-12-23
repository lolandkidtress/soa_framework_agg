package com.James.groovy;

import java.io.File;

import org.codehaus.groovy.control.CompilerConfiguration;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;


/**
 * Created by James on 2016/10/28.
 */
public class SimpleGroovyInvoker {

  private static GroovyClassLoader groovyClassLoader = null;

  public static void initGroovyClassLoader() {
    CompilerConfiguration config = new CompilerConfiguration();
    config.setSourceEncoding("UTF-8");
    // 设置该GroovyClassLoader的父ClassLoader为当前线程的加载器(默认)
    groovyClassLoader = new GroovyClassLoader(Thread.currentThread().getContextClassLoader(), config);
  }

  /**
   * 通过GroovyClassLoader加载GroovyShell_2，并反射调用其sayHello(String name, String sex, int age)方法
   *
   */
  public static String invokeSayHello() {
    String result = "";

    File groovyFile = new File("soa_groovy/src/main/java/com/James/groovy/groovyTrans");
    if (!groovyFile.exists()) {
      return result;
    }

    String jsonText = "{  " +
        "    \"contact_list\":[ " +
        "        { "+
        "            \"contact_noon\":0, "+
        "            \"phone_num_loc\":\"重庆\", "+
        "            \"contact_3m\":1, "+
        "            \"contact_1m\":0, "+
        "            \"contact_1w\":0, "+
        "            \"p_relation\":\"\", "+
        "            \"phone_num\":\"02347602738\", "+
        "            \"contact_name\":\"未知\", "+
        "            \"call_in_cnt\":1, "+
        "            \"call_out_cnt\":0, "+
        "            \"call_out_len\":0, "+
        "            \"contact_holiday\":0, "+
        "            \"needs_type\":\"未知\", "+
        "            \"contact_weekday\":1, "+
        "            \"contact_afternoon\":1, "+
        "            \"call_len\":3.3, "+
        "            \"contact_early_morning\":0, "+
        "            \"contact_night\":0, "+
        "            \"contact_3m_plus\":0, "+
        "            \"call_cnt\":1, "+
        "            \"call_in_len\":3.3, "+
        "            \"contact_all_day\":false, "+
        "            \"contact_morning\":0, "+
        "            \"contact_weekend\":0 "+
        "        }, "+
        "        { "+
        "            \"contact_noon\":3, "+
        "            \"phone_num_loc\":\"上海\", "+
        "            \"contact_3m\":1, "+
        "            \"contact_1m\":10, "+
        "            \"contact_1w\":1, "+
        "            \"p_relation\":\"\", "+
        "            \"phone_num\":\"02347607738\", "+
        "            \"contact_name\":\"未知\", "+
        "            \"call_in_cnt\":4, "+
        "            \"call_out_cnt\":12, "+
        "            \"call_out_len\":22.45, "+
        "            \"contact_holiday\":0, "+
        "            \"needs_type\":\"未知\", "+
        "            \"contact_weekday\":13, "+
        "            \"contact_afternoon\":4, "+
        "            \"call_len\":26.333333333333332, "+
        "            \"contact_early_morning\":0, "+
        "            \"contact_night\":0, "+
        "            \"contact_3m_plus\":4, "+
        "            \"call_cnt\":16, "+
        "            \"call_in_len\":3.8833333333333333, "+
        "            \"contact_all_day\":false, "+
        "            \"contact_morning\":9, "+
        "            \"contact_weekend\":3 "+
        "        } "+
        "    ], "+
        "    \"data_source\":[ "+
        "        { "+
        "            \"status\":\"valid\", "+
        "            \"account\":\"13883113556\", "+
        "            \"binding_time\":\"2007-04-11 00:00:00\", "+
        "            \"name\":\"重庆移动\", "+
        "            \"category_value\":\"移动运营商\", "+
        "            \"reliability\":\"实名认证\", "+
        "            \"key\":\"chinamobilecq\", "+
        "            \"category_name\":\"mobile\" "+
        "        } "+
        "    ], "+
        "    \"behavior_check\":[ "+
        "        { "+
        "            \"check_point\":\"朋友圈在哪里\", "+
        "            \"score\":0, "+
        "            \"result\":\"无数据\", "+
        "            \"evidence\":\"未提供居住地址\" "+
        "        } "+
        "    ], "+

        "   \"report\":{ "+
        "            \"token\":\"22c2672901f2472fad08e836d126b3e8\",  "+
        "            \"updt\":{  "+
        "                \"$date\":\"2016-10-20T10:08:56.000Z\"    "+
        "            },   "+
        "            \"id\":\"201610201808560008\", "+
        "            \"version\":\"4.1\"   "+
        "        } "+
        "} "
        ;

    try {
      // 获得GroovyShell_2加载后的class
      Class<?> groovyClass = groovyClassLoader.parseClass(groovyFile);
      // 获得GroovyShell_2的实例
      GroovyObject groovyObject = (GroovyObject) groovyClass.newInstance();
      // 反射调用sayHello方法得到返回值
      Object methodResult = groovyObject.invokeMethod("trans", new Object[] {jsonText});
      if (methodResult != null) {
        result = methodResult.toString();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return result;
  }

  public static void main(String[] args) throws Exception {

    initGroovyClassLoader();
    invokeSayHello();
    System.out.println(invokeSayHello());
  }
}
