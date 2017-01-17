package com.James.Filter;

import org.jboss.netty.util.internal.ConcurrentHashMap;

import com.James.basic.Model.Status;


/**
 * Created by James on 2016/10/14.
 */
public class mockFilter  {

  private static class InnerInstance {
    public static final mockFilter instance = new mockFilter();
  }

  //记录当前是否已降级
  private static final ConcurrentHashMap<String,RpcStatus> mock_statistic = new ConcurrentHashMap<>();

  public static mockFilter getInstance() {
    return InnerInstance.instance;
  }

  public boolean Filter(Status status) {

    return false;
  }


  public boolean isBlockedStatus(Status status) {
    mock_statistic.get(status.getName());
      return true;
  }

  public boolean failIncr(Status status) {
    return false;
  }

  public boolean cleanState(Status status) {
    return false;
  }
}
