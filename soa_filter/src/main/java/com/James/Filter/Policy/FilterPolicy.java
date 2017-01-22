package com.James.Filter.Policy;

/**
 * Created by James on 2017/1/19.
 */
public class FilterPolicy {

  public enum degradePolicy {
    //降级后直接返回
    BEFORE("before"),
    //降级后先尝试调用,失败后返回
    AFTER("after");

    private String Policy;
    private degradePolicy(String policy){
      this.Policy=policy;
    }

    public String getPolicy(){
      return this.Policy;
    }

  }


}
