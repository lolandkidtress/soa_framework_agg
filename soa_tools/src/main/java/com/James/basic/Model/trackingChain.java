package com.James.basic.Model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.James.basic.UtilsTools.JsonConvert;


/**
 * Created by James on 2017/2/4.
 * 用于记录调用链的实体对象
 */
public class TrackingChain {
  private static final Log logger = LogFactory.getLog(TrackingChain.class.getName());

  //RPC调用时,参数中带有rackingID
  //以下是服务发起的字段
  //分布式跟踪的key
  private String trackingID;
  //发起请求的客户端id
  private String invokerID;
  // 调用的服务名
  private String fromMethod;

  // 以下是被调用的服务
  private String clientID;
  // 调用的服务名和接口
  private String toClass;
  private String toMethod;

  //开始时间
  private long start_time;
  //结束时间
  private long end_time;
  //调用的步进,递增
  private Integer Sequence ;

  private boolean status=true;
  private String error_msg;

  public TrackingChain(){
    this.Sequence=0;
  }

  public TrackingChain(String trackingID){

    this.trackingID= trackingID;
    this.Sequence=0;

  }

  public String getTrackingID() {
    return trackingID;
  }

  public void setTrackingID(String trackingID) {
    this.trackingID = trackingID;
  }

  public String getInvokerID() {
    return invokerID;
  }

  public void setInvokerID(String invokerID) {
    this.invokerID = invokerID;
  }

  public String getFromMethod() {
    return fromMethod;
  }

  public void setFromMethod(String fromMethod) {
    this.fromMethod = fromMethod;
  }

  public String getClientID() {
    return clientID;
  }

  public void setClientID(String clientID) {
    this.clientID = clientID;
  }

  public String getToClass() {
    return toClass;
  }

  public void setToClass(String toClass) {
    this.toClass = toClass;
  }

  public String getToMethod() {
    return toMethod;
  }

  public void setToMethod(String toMethod) {
    this.toMethod = toMethod;
  }

  public long getStart_time() {
    return start_time;
  }

  public void setStart_time(long start_time) {
    this.start_time = start_time;
  }

  public long getEnd_time() {
    return end_time;
  }

  public void setEnd_time(long end_time) {
    this.end_time = end_time;
  }

  public Integer getSequence() {
    return Sequence;
  }

  public void setSequence(Integer sequence) {
    Sequence = sequence;
  }

  public boolean isStatus() {
    return status;
  }

  public void setStatus(boolean status) {
    this.status = status;
  }

  public String getError_msg() {
    return error_msg;
  }

  public void setError_msg(String error_msg) {
    this.error_msg = error_msg;
  }

  public String toJson() {
    try {
      return JsonConvert.toJson(this);
    } catch (Exception e) {
      logger.error("json 解析失败:", e);
      return "";
    }
  }
}
