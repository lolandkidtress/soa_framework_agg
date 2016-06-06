package com.James.Invoker;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.James.HashFunction.IHashFunction;
import com.James.Model.SharedProvider;
import com.James.basic.UtilsTools.CommonConfig;
import com.James.zkInstance;
import com.James.zkTools.zkClientTools;


/**
 * Created by James on 16/6/2.
 */
public class Invoker {

  private static final Logger LOGGER = LoggerFactory.getLogger(Invoker.class.getName());

  private final String zk_root_dir = zkInstance.INSTANCE.providerMangerPath;

  public IHashFunction algo = IHashFunction.MURMUR_HASH;

  //一致性hash环
  public TreeMap<Long, SharedProvider> TreeMapNodes = new TreeMap<>();

  public TreeMap<Long, SharedProvider> getTreeMap(){
    return this.TreeMapNodes;
  }

  //数字越大,虚拟节点越多,分布越平均
  public static final int basic_virtual_node_number = 40;

  private zkClientTools zkclient;


  public Invoker(String server_name){
    InvokerHelper.INSTANCE.init();

    this.zkclient = zkInstance.INSTANCE.getZkclient();



    try{

      List<String> ChildrenPaths = this.zkclient.getChildren(zk_root_dir);
      for(String childrenPath : ChildrenPaths){
        LOGGER.info(childrenPath);
      }


    }catch(Exception e){

    }



//    iListeners lsrner = new dataChangedListener();
//    StringBuilder sb = new StringBuilder().append(zk_root_dir);
//    sb.append(CommonConfig.SLASH);
//    sb.append(server_name);
//    InvokerHelper.INSTANCE.watchZKConfigConnectStat(sb.toString(),lsrner);
//    InvokerHelper.INSTANCE.watchZKConfigDataChange(sb.toString(), lsrner);
//    InvokerHelper.INSTANCE.watchZKConfigChildChange(sb.toString(), lsrner);



  }

  public static Invoker create(String server_name){
    return new Invoker(server_name);
  }

  //在环上获取节点
  public SharedProvider get(String seed) {
    //TODO
    //if ava_node.size=0; remap

    SortedMap<Long, SharedProvider> tail = TreeMapNodes.tailMap(algo.hash(seed.getBytes(CommonConfig.CHARSET)));
    if (tail.isEmpty()) {
      Map.Entry<Long, SharedProvider> firstEntry = TreeMapNodes.firstEntry();
      if (firstEntry != null) {
        return firstEntry.getValue();
      }
      return null;
    }
    return tail.get(tail.firstKey());
  }

  //计算一致性hash的key后加入环中
  public void add(SharedProvider sharedProvider) {
    for (int n = 0; n < basic_virtual_node_number ; n++) {
      try {
        Long key = this.algo.hash(
            new StringBuilder(sharedProvider.getIdentityID())
                .append("*")
                .append(n).toString()
            , CommonConfig.CHARSET);

        TreeMapNodes.put(key, sharedProvider);

      } catch (UnsupportedEncodingException e) {
        LOGGER.error("添加节点失败", e);
      }
    }
  }

  //从环中删除
  public void remove(SharedProvider sharedProvider) {
    for (int n = 0; n < basic_virtual_node_number ; n++) {
      try {
        Long key = this.algo.hash(
            new StringBuilder(sharedProvider.getIdentityID())
                .append("*")
                .append(n).toString()
            , CommonConfig.CHARSET);

        TreeMapNodes.remove(key);
      } catch (UnsupportedEncodingException e) {
        LOGGER.error("删除节点失败", e);
      }
    }
  }


}
