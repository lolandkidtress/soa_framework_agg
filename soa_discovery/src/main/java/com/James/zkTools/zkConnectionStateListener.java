package com.James.zkTools;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.James.Listeners.iListeners;
import com.James.basic.UtilsTools.CommonConfig;


/**
 * Created by James on 16/5/27.
 * 连接事件
 */
public class zkConnectionStateListener implements ConnectionStateListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(zkConnectionStateListener.class.getName());
	
	private iListeners fireListener;
	private String path;
	
	public zkConnectionStateListener(String path ,iListeners after){
		this.fireListener=after;
		this.path = path;
	}

	public void stateChanged(CuratorFramework client, ConnectionState newState) {
		LOGGER.debug("zk连接状态为:" + newState);


		if(newState==ConnectionState.CONNECTED || newState ==ConnectionState.RECONNECTED){
			LOGGER.debug("zk恢复连接");
			fireListener.ConnectionRecover(path, CommonConfig.zkEventType.ConnectionRecover.name());

//			iListeners lsrner = new dataChangedListener();
//			InvokerHelper.INSTANCE.watchZKConfigConnectStat(path, lsrner);
		}else{
			//Lost或者Suspend
			LOGGER.debug("zk失去连接");
			fireListener.ConnectionLost(path, CommonConfig.zkEventType.ConnectionLost.name());

//			iListeners lsrner = new dataChangedListener();
//			InvokerHelper.INSTANCE.watchZKConfigConnectStat(path, lsrner);
		}

	}

}
