package com.James.avroNettyClientConnect;

import com.James.avroProto.avrpRequestProto;


/**
 * Created by James on 2017/1/23.
 */
public interface ConnectPoolDelegate {
  // 获得连接
  public avrpRequestProto getConnection();
  // 回收连接
  public void releaseConn(avrpRequestProto conn) ;
  // 销毁清空
  public void destroy();
  // 连接池是活动状态
  public boolean isActive();
  // 定时器，检查连接池
  public void cheackPool();
}
