package com.James.basic.Model;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.James.basic.Exception.Method_Not_Found_Exception;
import com.James.basic.HashFunction.iHashFunction;
import com.James.basic.HashFunction.MurmurHash;
import com.James.basic.UtilsTools.CommonConfig;


/**
 * Created by James on 16/6/2.
 * 有多个sharedProvider组成的一组服务提供组
 * 通过一致性hash实现负载均衡
 */
public class Provider {

  private static final Logger LOGGER = LogManager.getLogger(Provider.class.getName());

  public iHashFunction algo =  new MurmurHash();


  //key:version,value:hash环
  private ConcurrentHashMap<String,TreeMap> methodTreeMapNodes= new ConcurrentHashMap();

//  //一致性hash环
//  public TreeMap<Long, SharedNode> TreeMapNodes = new TreeMap<>();
//
//  public TreeMap<Long, SharedNode> getTreeMap(){
//    return this.TreeMapNodes;
//  }

  //数字越大,虚拟节点越多,分布越平均
  public static final int basic_virtual_node_number = 20;

  public Provider(){

  }

  //每个version一个hash环
  public Provider init(String version,SharedNode SharedNode){
    TreeMap<Long, com.James.basic.Model.SharedNode> TreeMapNodes = methodTreeMapNodes.getOrDefault(version,new TreeMap<>());
    TreeMapNodes = add(TreeMapNodes, SharedNode);
    methodTreeMapNodes.put(version,TreeMapNodes);

    return this;
  }


  //在环上获取节点
  public SharedNode get(String version,String seed) throws Method_Not_Found_Exception {
    TreeMap TreeMapNodes = methodTreeMapNodes.get(version);
    if(TreeMapNodes==null){
      throw new Method_Not_Found_Exception();
    }
    SortedMap<Long, SharedNode> tail = TreeMapNodes.tailMap(algo.hash(seed.getBytes(CommonConfig.CHARSET)));
    if (tail.isEmpty()) {
      Map.Entry<Long, SharedNode> firstEntry = TreeMapNodes.firstEntry();
      if (firstEntry != null) {
        return firstEntry.getValue();
      }
      throw new Method_Not_Found_Exception();
    }
    return tail.get(tail.firstKey());
  }

  //计算一致性hash的key后加入环中
  private TreeMap add(TreeMap TreeMapNodes ,SharedNode SharedNode) {
    for (int n = 0; n < basic_virtual_node_number ; n++) {
      try {
        Long key = this.algo.hash(
            new StringBuilder(SharedNode.getIdentityID())
                .append("*")
                .append(n).toString()
            , CommonConfig.CHARSET);

        TreeMapNodes.put(key, SharedNode);

      } catch (UnsupportedEncodingException e) {
        LOGGER.error("添加节点失败", e);

      }
    }
    return TreeMapNodes;
  }

  //从环中删除
  public TreeMap remove(String version,SharedNode SharedNode) {
    TreeMap TreeMapNodes = methodTreeMapNodes.get(version);
    for (int n = 0; n < basic_virtual_node_number ; n++) {
      try {
        Long key = this.algo.hash(
            new StringBuilder(SharedNode.getIdentityID())
                .append("*")
                .append(n).toString()
            , CommonConfig.CHARSET);

        TreeMapNodes.remove(key);

      } catch (UnsupportedEncodingException e) {
        LOGGER.error("删除节点失败", e);

      }
    }
    return TreeMapNodes;
  }

  //TODO
  //节点不可用时的处理

  //TODO
  //定时扫描不可用节点


  //setting & getter

  public ConcurrentHashMap<String, TreeMap> getMethodTreeMapNodes() {
    return methodTreeMapNodes;
  }

  public void setMethodTreeMapNodes(ConcurrentHashMap<String, TreeMap> methodTreeMapNodes) {
    this.methodTreeMapNodes = methodTreeMapNodes;
  }
}
