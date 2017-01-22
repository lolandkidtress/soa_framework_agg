package com.James.Filter.abstractCountDown;

/**
 * Created by James on 2017/1/19.
 */
public interface CountDownDelegate {
  public abstract boolean isFreezingStatus() ;

  public abstract void failIncr() ;

  public abstract void cleanState() ;
}
