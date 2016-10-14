package com.James.mock;

import java.util.concurrent.ConcurrentSkipListMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.recipes.shared.SharedCount;
import org.apache.curator.framework.recipes.shared.SharedCountListener;

import com.James.Listener.mockCountListener;
import com.James.basic.Model.mockPolicy;
import com.James.basic.UtilsTools.CommonConfig;
import com.James.basic.zkTools.zkClientTools;


/**
 * Created by James on 16/9/29.
 */
public class mockInstance {
  private static final Log LOGGER = LogFactory.getLog(mockInstance.class.getName());

  //zk中每一个mockPolicy会有一个sharedCount,通过监听变化来维护mockFailMap中每个mockpolicy的计数
  private static ConcurrentSkipListMap<String,mockPolicy> mockFailMap  = new ConcurrentSkipListMap<>();
  //保存每个
  private static ConcurrentSkipListMap<String,Long> mockFailTimeMap  = new ConcurrentSkipListMap<>();

  //记录是否已经对计数器添加过监听
  private static ConcurrentSkipListMap<String,SharedCount> mockSharedCountMap  = new ConcurrentSkipListMap<>();
  private zkClientTools zkclient;

//  private static class InnerInstance {
//    public static final mockInstance instance = mockInstance.create();
//  }
//
//  public static mockInstance getInstance() {
//    return InnerInstance.instance;
//  }

  public mockInstance(String zkconnect){
    if (!zkClientTools.isConnected(zkconnect)) {
      LOGGER.error("zookeeeper连接失败");
    }
    this.zkclient = new zkClientTools(zkconnect, "");
    LOGGER.info("zookeeeper连接成功");
  }

  //检查是否已降级
  //true  通过限制
  //false 超过限制
  public boolean validateMockState(String MockPolicyName){
    String p_MockPolicyName = CommonConfig.SLASH.concat(MockPolicyName);
    mockPolicy  mlMap = mockFailMap.get(p_MockPolicyName);
    if(mlMap==null){
      return false;
    }
    return mlMap.Check();
  }


  //调用异常后,在zk上更新计数器
  //通过对计数器的监听同步到本地
  public boolean failIncr(mockPolicy MockPolicy){
    String p_MockPolicyName = CommonConfig.SLASH.concat(MockPolicy.getName());
    try{

      mockPolicy _MockPolicy = mockFailMap.get(p_MockPolicyName);
      if(_MockPolicy==null){
        _MockPolicy = MockPolicy;
        return initMockState(p_MockPolicyName);
      }else{
        return incrMockState(p_MockPolicyName);
      }

    }catch(Exception e){
      e.printStackTrace();
      LOGGER.error("zk创建"+p_MockPolicyName+"计数器失败",e);
      return false;
    }

  }

  //zk上写入mock
  public boolean initMockState(String MockPolicyName){
    try{
      zkclient.setShardCount(MockPolicyName, 0);
      zkclient.incrShardCount(MockPolicyName);

      SharedCount sharedCount = mockSharedCountMap.get(MockPolicyName);
      if(sharedCount==null){
        zkclient.closeSharedCount(sharedCount);
      }


      SharedCountListener lsr = new mockCountListener();
        //针对这个降级节点监听
      sharedCount = zkclient.addLsrnOnSharedCount(MockPolicyName, lsr);

      mockSharedCountMap.put(MockPolicyName, sharedCount);

      return true;
    }catch(Exception e){
      e.printStackTrace();
      LOGGER.error("zk创建"+MockPolicyName+"计数器失败",e);
      return false;
    }

  }

  //TODO
  public boolean incrMockState(String MockPolicyName){
    return false;
  }

  //TODO
  //恢复或者配置变更时
  //清除mock状态
  public boolean removeMockState(String MockPolicyName){

    return false;
  }




}
