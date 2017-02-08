package com.James.Filter;

import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.util.internal.ConcurrentHashMap;

import com.James.Filter.degrade.degradeCountDown;
import com.James.Filter.rateLimit.ratelimitCountDown;


/**
 * Created by James on 2016/10/14.
 * 单例
 * 保存降级和流量的配置
 */
public class Filter {

  private static final Log logger = LogFactory.getLog(Filter.class.getName());

  private static class InnerInstance {
    public static final Filter instance = new Filter();
  }

  public static Filter getInstance() {
    return InnerInstance.instance;
  }

  //记录配置的降级策略
  private static final ConcurrentHashMap<String,degradeCountDown> degrade_Config = new ConcurrentHashMap<>();
  //记录配置的限流策略
  private static final ConcurrentHashMap<String,ratelimitCountDown> limit_Config = new ConcurrentHashMap<>();

  public Filter() {
  }

  public void addLimitConfig(ratelimitCountDown RatelimitCountDown){
    limit_Config.put(RatelimitCountDown.getName(),RatelimitCountDown);
  }

  public ratelimitCountDown getLimitConfig(String LimitConfigName){
    return limit_Config.get(LimitConfigName);
  }

  public void addDegradeConfig(degradeCountDown DegradeCountDown) {
    degrade_Config.put(DegradeCountDown.getName(), DegradeCountDown);
  }

  public degradeCountDown getDegradeCountDown(String DegradeConfigName){
    return degrade_Config.get(DegradeConfigName);
  }

  //检查是否超过流量阈值
  public boolean isPassedRateLimit(String limitName){
    ratelimitCountDown rlcd = limit_Config.get(limitName);

    if(rlcd!=null){
      boolean flg = rlcd.isFreezingStatus();
      limit_Config.put(limitName,rlcd);
      logger.debug("limit返回" + flg);
      return !flg;
    }
    return true;

  }

  //检查是否配置过降级
  public boolean isPassedDegrade(String degradeName){
    degradeCountDown dcd = degrade_Config.get(degradeName);
    if(dcd!=null){
      boolean flg = dcd.isFreezingStatus();
      degrade_Config.put(degradeName,dcd);
      logger.debug("degrade返回"+flg);
      return !flg;
    }
    return true;
  }

  //限流策略在每次校验时自动更新次数
  public void IncrLimitCount(String limitName){
  }

  //降级策略需要在调用失败后手动更新次数
  public void IncrDegradeCount(String degradeName) {
    logger.debug("degradeName+1");
    degradeCountDown dcd = degrade_Config.get(degradeName);
    dcd.failIncr();
    degrade_Config.put(degradeName,dcd);
  }

  public static void main(String[] args) throws Exception {
    degradeCountDown dcd = new degradeCountDown("degrade",5000, 1, 5000,200,"degradeFail");
    ratelimitCountDown rcd = new ratelimitCountDown("rate",5000, 1, 5000,200,"rateFail");
    Filter.getInstance().addDegradeConfig(dcd);
    Filter.getInstance().addLimitConfig(rcd);

    System.out.println(Filter.getInstance().isPassedRateLimit("rate"));
    System.out.println(Filter.getInstance().isPassedRateLimit("rate"));

    System.out.println(Filter.getInstance().isPassedDegrade("degrade"));
    System.out.println(Filter.getInstance().isPassedDegrade("degrade"));
    Filter.getInstance().IncrDegradeCount("degrade");
    System.out.println(Filter.getInstance().isPassedDegrade("degrade"));
    TimeUnit.SECONDS.sleep(6);
    System.out.println(Filter.getInstance().isPassedDegrade("degrade"));
    System.out.println(Filter.getInstance().isPassedDegrade("degrade"));

  }

}
