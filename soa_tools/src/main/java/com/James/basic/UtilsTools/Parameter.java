package com.James.basic.UtilsTools;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by James on 16/6/16.
 * 传入的参数类型
 */
public class Parameter extends IdentityHashMap<String, String> {
  private static final long serialVersionUID = 1L;


  public Parameter(){
    //trackingID
    //用于调用服务时,作为一致性hash的key
    if(!super.containsKey(CommonConfig.s_trackingID)) {
      super.put(CommonConfig.s_trackingID, Utils.generateTrackingID());
    }
  }
  public static Parameter create() {

    return new Parameter();
  }

  public static Parameter create(Map<String, List<String>> name_value_pair) {
    Parameter identityhashmap = new Parameter();
    name_value_pair.forEach((key, values) -> {
      values.forEach(value -> {
        identityhashmap.put(new String(key), value);
      });
    });

    //
    return identityhashmap;
  }

  public static Parameter create(String key, String value) {
    return new Parameter().add(key, value);
  }

  public Parameter add(String key, String value) {
    super.put(key, value);
    return this;
  }

  @Override
  public String put(String key, String value) {
    super.put(key, value);
    return value;
  }
}
