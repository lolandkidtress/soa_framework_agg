package com.James.zkTools;

import com.James.Listeners.iListeners;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by James on 16/5/27.
 * 连接时间
 */
public class zkConnectionStateListener implements ConnectionStateListener {
	private static final Logger logger = LoggerFactory.getLogger(zkConnectionStateListener.class.getName());
	
	private iListeners fireListener;
	
	public zkConnectionStateListener(iListeners after){
		fireListener=after;
	}
	
	public void stateChanged(CuratorFramework client, ConnectionState newState) {
		logger.debug("zk连接状态为:" + newState);


		if(newState==ConnectionState.CONNECTED || newState ==ConnectionState.RECONNECTED){
			logger.debug("zk恢复连接");
			fireListener.ConnectionRecover();
		}else{
			//Lost或者Suspend
			logger.debug("zk失去连接");
			fireListener.ConnectionLost();
		}
	
		
		
	}

}
