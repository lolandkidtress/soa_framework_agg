package com.James.Model;

import com.James.basic.Annotation.mockAnnotation;


/**
 * Created by James on 16/8/26.
 * 降级策略 model
 */
public class mockPolicy {

  private String name ;
  private mockAnnotation.Policy policy;

  //时间窗口 ms
  private int allowFailPeriod = 5000;
  //时间窗口内允许失败的次数
  private int allowFailTimes = 5;
  //降级持续时间 ms
  private int freezingTime = 5000;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public mockAnnotation.Policy getPolicy() {
    return policy;
  }

  public void setPolicy(mockAnnotation.Policy policy) {
    this.policy = policy;
  }

  public int getAllowFailPeriod() {
    return allowFailPeriod;
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

  @Override
  public String toString() {
    return "mockConfig{" +
        "name='" + name + '\'' +
        ", policy=" + policy +
        ", allowFailPeriod=" + allowFailPeriod +
        ", allowFailTimes=" + allowFailTimes +
        ", freezingTime=" + freezingTime +
        '}';
  }
}
