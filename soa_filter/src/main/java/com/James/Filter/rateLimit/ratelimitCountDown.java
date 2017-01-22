package com.James.Filter.rateLimit;

import com.James.Filter.abstractCountDown.AbstractCountDown;
import com.James.Filter.abstractCountDown.CountDownDelegate;
import com.James.basic.UtilsTools.Return;


/**
 * Created by James on 2017/1/19.
 *
 * 限流用
 */
public class ratelimitCountDown extends AbstractCountDown implements CountDownDelegate {

  //初始化
  public ratelimitCountDown(String name,int AllowFailPeriod, int AllowFailTimes, int FreezingTime,Integer code, String note){
    this.name=name;
    this.allowlPeriod = AllowFailPeriod;
    this.allowTimes = AllowFailTimes;
    this.freezingTime = FreezingTime;

    this.failedCount.set(0);

    this.defaultReturn = Return.FAIL(code, note);

  }



  @Override
  public boolean isFreezingStatus() {
    long currentTime = System.currentTimeMillis();

    //有调用记录
    if(this.lastFailedTime>0 ){
      //冻结状态的记录
      if(this.lastFreezingTime >0){
        //还在冻结状态中
        if((currentTime-this.lastFreezingTime ) < this.freezingTime){
          return true;
        }else{
          //冻结已过期
          this.lastFreezingTime=0L;
          this.failedCount.incrementAndGet();
          return false;
        }
      }else{
        //没有在冻结状态的记录
        //判断窗口时间内是否超过限制次数
        if((currentTime-this.lastFailedTime)>allowlPeriod){
          //过了窗口期
          //初始化
          this.lastFailedTime=currentTime;
          this.failedCount.set(0);
          this.failedCount.incrementAndGet();
          return false;
        }else{
          if(this.failedCount.incrementAndGet()>this.allowTimes){
            //超过限制
            return true;
          }else{
            //没有超过限制
            return false;
          }
          //计算次数有没有超过阈值
        }
      }
    }else{
      //第一次调用
      this.lastFailedTime = currentTime;
      this.failedCount.set(0);
      this.failedCount.incrementAndGet();
      return false;
    }

  }

  @Override
  public void failIncr(){

  }


  @Override
  public void cleanState() {
    failedCount.set(0);
    lastFailedTime = 0L;
    lastFreezingTime = 0L;
  }
}
