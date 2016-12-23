package com.James.Filter;

import org.jboss.netty.util.internal.ConcurrentHashMap;

import com.James.basic.Filter.Filter;
import com.James.basic.Model.Status;


/**
 * Created by James on 2016/10/14.
 */
public class mockFilter implements Filter {

  private static class InnerInstance {
    public static final mockFilter instance = new mockFilter();
  }


  //记录当前是否已降级
  private static final ConcurrentHashMap<String,RpcStatus> mock_statistic = new ConcurrentHashMap<>();


  public static mockFilter getInstance() {
    return InnerInstance.instance;
  }

  @Override
  public boolean Filter(Status status) {

    return false;
  }


  @Override
  public boolean isBlockedStatus(Status status) {
    mock_statistic.get(status.getName());
      return true;
  }

  @Override
  public boolean failIncr(Status status) {
    return false;
  }

  @Override
  public boolean cleanState(Status status) {
    return false;
  }
}
