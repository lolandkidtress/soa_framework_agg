package com.James.zkTools;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.James.Listeners.iListeners;
import com.James.basic.UtilsTools.CommonConfig;


/**
 * Created by James on 16/5/27.
 * 子节点变更事件
 */
public class zkChildChangedListener implements CuratorListener {
	private static final Logger logger = LoggerFactory.getLogger(zkChildChangedListener.class.getName());

	private iListeners fireListener;

	private String path;

	public zkChildChangedListener(String path,iListeners after){
		this.path = path;
		this.fireListener=after;
	}
	
    public void eventReceived(CuratorFramework zkTools, CuratorEvent event) throws Exception
    {
    	
    	WatchedEvent watchevent = event.getWatchedEvent();

		if(watchevent.getType().equals(EventType.NodeChildrenChanged)){
			fireListener.ChildChanged(path,CommonConfig.zkEventType.NodeChildrenChanged.name());
//			iListeners lsrner = new dataChangedListener();
//			InvokerHelper.INSTANCE.watchZKConfigChildChange(path, lsrner);
		}
    	
    }

}
