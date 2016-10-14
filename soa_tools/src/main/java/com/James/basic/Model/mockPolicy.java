package com.James.basic.Model;

import com.James.basic.Annotation.mockAnnotation;
import com.James.basic.Enum.Code;
import com.James.basic.UtilsTools.Return;


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

  //降级时的返回值
  private Return mockReturn = Return.FAIL(Code.over_limit.code,Code.over_limit.note);


  public mockPolicy(){

  }




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

  public Return getMockReturn() {
    return mockReturn;
  }

  public void setMockReturn(Return mockReturn) {
    this.mockReturn = mockReturn;
  }


  //true 不需要降级
  //false 需要降级
  //判断是否
  public boolean Check() {
//
//    //判断是否在上次的时间窗口内
//    Timestamp currTimestamp = new Timestamp(Clock.system(Configuration.zoneid).millis());
//    if( (currTimestamp.getTime() - this.lastTime.getTime())  > TimePeriod ) {
//      //不在上次的时间窗口内
//      this.lastTime = currTimestamp;
//      this.totalCount.addAndGet(1);
//      this.perCount.set(0);
//      logger.info("新的时间窗口");
//      return true;
//    }else{
//
//      this.totalCount.addAndGet(1);
//      this.perCount.addAndGet(1);
//
//      if(this.perCount.get() > this.limit ){
//        logger.info("超过限制：" + this.perCount.get() );
//        return false;
//      }else{
//        return true;
//      }
//    }
//  }
  return true;
  }

  //TODO 记录调用失败
  public void failIncr(){

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
