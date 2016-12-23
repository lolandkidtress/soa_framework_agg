package com.James.basic.Model;

import java.util.concurrent.atomic.AtomicLong;


/**
 * Created by James on 2016/10/21.
 */
public abstract class Status {

  public String name ;

  //计算周期 ms
  public int allowFailPeriod = 5000;
  //时间窗口内允许失败的次数
  public int allowFailTimes = 5;
  //冻结周期 ms
  public int freezingTime = 5000;

  public static AtomicLong failedCount = new AtomicLong();

  //上一次开始计算失败的时间
  public static Long lastFailedTime =0L;

  //冻结开始时间
  public static Long lastFreezingTime =0L;

  public int getAllowFailPeriod() {
    return allowFailPeriod;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setAllowFailPeriod(int allowFailPeriod) {
    this.allowFailPeriod = allowFailPeriod;
  }

  public int getAllowFailTimes() {
    return allowFailTimes;
  }

  public void setAllowFailTimes(int allowFailTimes) {
    this.allowFailTimes = allowFailTimes;
  }

  public int getFreezingTime() {
    return freezingTime;
  }

  public void setFreezingTime(int freezingTime) {
    this.freezingTime = freezingTime;
  }

  public static AtomicLong getFailedCount() {
    return failedCount;
  }

  public static void setFailedCount(AtomicLong failedCount) {
    Status.failedCount = failedCount;
  }

  public static Long getLastFailedTime() {
    return lastFailedTime;
  }

  public static void setLastFailedTime(Long lastFailedTime) {
    Status.lastFailedTime = lastFailedTime;
  }

  public static Long getLastFreezingTime() {
    return lastFreezingTime;
  }

  public static void setLastFreezingTime(Long lastFreezingTime) {
    Status.lastFreezingTime = lastFreezingTime;
  }

  public abstract boolean isBlockedStatus() ;

  public abstract void failIncr() ;

  public abstract void cleanState() ;
}
