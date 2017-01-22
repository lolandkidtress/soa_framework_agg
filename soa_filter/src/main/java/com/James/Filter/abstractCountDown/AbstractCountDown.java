package com.James.Filter.abstractCountDown;

import java.util.concurrent.atomic.AtomicLong;

import com.James.basic.UtilsTools.Return;


/**
 * Created by James on 2016/10/21.
 * 计数器
 */
public abstract class AbstractCountDown {

  public String name;
  //计算周期 ms
  public int allowlPeriod = 5000;
  //时间窗口内允许的次数
  public int allowTimes = 5000;
  //冻结周期 ms
  public int freezingTime = 5000;

  public static AtomicLong failedCount = new AtomicLong();

  //上一次开始计算失败的时间
  public static Long lastFailedTime =0L;
  //冻结开始时间
  public static Long lastFreezingTime =0L;

  public Return defaultReturn;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getAllowFailPeriod() {
    return allowlPeriod;
  }
  public void setAllowFailPeriod(int allowFailPeriod) {
    this.allowlPeriod = allowlPeriod;
  }

  public int getAllowFailTimes() {
    return allowTimes;
  }

  public void setAllowFailTimes(int allowFailTimes) {
    this.allowTimes = allowTimes;
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
    AbstractCountDown.failedCount = failedCount;
  }

  public static Long getLastFailedTime() {
    return lastFailedTime;
  }

  public static void setLastFailedTime(Long lastFailedTime) {
    AbstractCountDown.lastFailedTime = lastFailedTime;
  }

  public static Long getLastFreezingTime() {
    return lastFreezingTime;
  }

  public static void setLastFreezingTime(Long lastFreezingTime) {
    AbstractCountDown.lastFreezingTime = lastFreezingTime;
  }

  public Return getDefaultReturn() {
    return defaultReturn;
  }

  public void setDefaultReturn(Return defaultReturn) {
    this.defaultReturn = defaultReturn;
  }
}
