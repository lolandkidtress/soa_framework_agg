package com.James.Filter;

import com.James.basic.Model.Status;


/**
 * Created by James on 2016/10/21.
 *
 * 降级/限流
 * 计数对象
 */
public class RpcStatus extends Status {

  //规定时间内失败的次数


  public RpcStatus(int AllowFailPeriod,int AllowFailTimes,int FreezingTime){
    this.allowFailPeriod = AllowFailPeriod;
    this.allowFailTimes = AllowFailTimes;
    this.freezingTime = FreezingTime;

    super.failedCount.set(0);

  }

  @Override
  public boolean isBlockedStatus() {
    long currentTime = System.currentTimeMillis();

    if(lastFreezingTime >0L){
      //冻结状态
      if((currentTime - lastFreezingTime) <=  freezingTime){
        return true;
      }
      //不在冻结状态
      if((currentTime - lastFreezingTime) >  freezingTime){
        lastFreezingTime=0L;
      }
      return false;
    }else{
      return false;
    }

  }

  @Override
  public void failIncr(){
    long currentTime = System.currentTimeMillis();

    if(lastFreezingTime>0L) {
      //冻结
      if(currentTime - lastFreezingTime >  freezingTime ){
        //冻结失效 重新开始计数
        lastFreezingTime = 0L;
        failedCount.set(0);
        failedCount.incrementAndGet();
        lastFailedTime = currentTime;
      }
    }else{

      if(lastFailedTime>=0){
        //已经开始记录错误了
        if (failedCount.incrementAndGet() > allowFailTimes) {
          //进入冻结
          lastFreezingTime = currentTime;

        } else {
          failedCount.incrementAndGet();
        }
      }else{
        //新的记录
        failedCount.set(0);
        failedCount.incrementAndGet();
        lastFailedTime = currentTime;
      }

    }

  }


  @Override
  public void cleanState() {
    failedCount.set(0);
    lastFailedTime = 0L;
    lastFreezingTime = 0L;
  }
}
