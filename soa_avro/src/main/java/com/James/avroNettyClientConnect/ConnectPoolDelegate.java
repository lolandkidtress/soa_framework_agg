package com.James.avroNettyClientConnect;

import com.James.avroProto.avrpRequestProto;


/**
 * Created by James on 2017/1/23.
 */
public interface ConnectPoolDelegate {
  // 获得连接
  avrpRequestProto getConnection();
  // 回收连接
  void releaseConn(avrpRequestProto conn) ;
  // 销毁清空
  void destroy();
  // 连接池是活动状态
  boolean isActive();
  // 定时器，检查连接池
  void cheackPool();
}
