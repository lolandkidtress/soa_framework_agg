package com.James.zkWatch;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.James.Listeners.iListeners;


/**
 * Created by James on 16/8/9.
 */
public class zkWatch implements CuratorWatcher {
  private final String path;
  private CuratorFramework zkTools;

  //watch事件处理类
  private iListeners fireListener;

  private static final Log LOGGER = LogFactory.getLog(zkWatch.class.getName());

  public zkWatch(String Path, CuratorFramework ZkTools, iListeners after){
    this.fireListener=after;
    this.path = Path;
    this.zkTools = ZkTools;
  }

  public String getPath() {
    return path;
  }


  /*
                    case -1: return EventType.None;
                    case  1: return EventType.NodeCreated;
                    case  2: return EventType.NodeDeleted;
                    case  3: return EventType.NodeDataChanged;
                    case  4: return EventType.NodeChildrenChanged;
   */
  @Override
  public void process(WatchedEvent event) throws Exception {
    LOGGER.info("watch接收到事件" + event.getType());

    //watch触发后,重新添加watch
    if(event.getType() == Watcher.Event.EventType.NodeDataChanged) {
      zkWatchInstance.getInstance().addDataWatch(path,zkTools);
    }
    if(event.getType() == Watcher.Event.EventType.NodeChildrenChanged) {
      zkWatchInstance.getInstance().addChildWatch(path,zkTools);
    }

    fireListener.Handle(path, event.getType().toString());

//      if(event.getType() == Watcher.Event.EventType.NodeDataChanged){
//        byte[] data = zkTools.
//            getData().
//            usingWatcher(this).forPath(path);
//        System.out.println(path+":"+new String(data,Charset.forName("utf-8")));
//      }
  }

}