package com.James.basic.Model;

import com.James.basic.Annotation.mockFilterAnnotation;
import com.James.basic.Enum.Code;
import com.James.basic.UtilsTools.Return;


/**
 * Created by James on 16/8/26.
 * 降级策略 model
 */
public class mockPolicy {

  private mockFilterAnnotation.Policy policy;

  //降级时的返回值
  private Return mockReturn = Return.FAIL(Code.over_limit.code,Code.over_limit.note);


  public mockPolicy(){

  }

  public mockFilterAnnotation.Policy getPolicy() {
    return policy;
  }

  public void setPolicy(mockFilterAnnotation.Policy policy) {
    this.policy = policy;
  }

  public Return getMockReturn() {
    return mockReturn;
  }

  public void setMockReturn(Return mockReturn) {
    this.mockReturn = mockReturn;
  }

//  @Override
//  public String toString() {
//    return "mockConfig{" +
//        "name='" + name + '\'' +
//        ", policy=" + policy +
//        ", allowFailPeriod=" + allowFailPeriod +
//        ", allowFailTimes=" + allowFailTimes +
//        ", freezingTime=" + freezingTime +
//        '}';
//  }


}
