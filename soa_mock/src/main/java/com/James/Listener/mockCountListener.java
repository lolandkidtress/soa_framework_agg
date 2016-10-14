package com.James.Listener;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.shared.SharedCountListener;
import org.apache.curator.framework.recipes.shared.SharedCountReader;
import org.apache.curator.framework.state.ConnectionState;


/**
 * Created by James on 16/9/30.
 */
public class mockCountListener implements SharedCountListener {
  @Override
  public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
    System.out.println("ShardCount connectionState changed: " + connectionState.toString());
  }


  //检测到计数变化后,进行判断
  @Override
  public void countHasChanged(SharedCountReader sharedCountReader, int i)
      throws Exception {
    System.out.println("Counter's value is changed to " + i);
    //TODO
    //数字变化时
  }
}
