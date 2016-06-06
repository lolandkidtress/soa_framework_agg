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
 * 数据变更事件
 */
public class zkDataChangedListener implements CuratorListener {
	private static final Logger logger = LoggerFactory.getLogger(zkDataChangedListener.class.getName());
	
	private iListeners fireListener;
	private String path;
	
	public zkDataChangedListener(String path,iListeners after){
		fireListener=after;
		this.path = path;
	}
	
    public void eventReceived(CuratorFramework zkTools, CuratorEvent event) throws Exception
    {
    	
    	WatchedEvent watchevent = event.getWatchedEvent();
		if(watchevent.getType().equals(EventType.NodeDataChanged)){
			fireListener.DataChanged(path,CommonConfig.zkEventType.DataChanged.name());
//    		refreshZKConfig();
//    		watchZKConfig(this);
    	}
    	
    	if(watchevent.getType().equals(EventType.NodeDeleted)){
    		fireListener.DataChanged(path,CommonConfig.zkEventType.DataChanged.name());
//    		refreshZKConfig();
//    		watchZKConfig(this);
    	}
    	
    	if(watchevent.getType().equals(EventType.NodeCreated)){
    		fireListener.DataChanged(path,CommonConfig.zkEventType.DataChanged.name()); ;
//    		refreshZKConfig();
//    		watchZKConfig(this);
    	}
    	
    	if(watchevent.getType().equals(EventType.None)){
//    		fireListener.DataChanged();
//    		refreshZKConfig();
//    		watchZKConfig(this);
    	}
    	
    }

}
