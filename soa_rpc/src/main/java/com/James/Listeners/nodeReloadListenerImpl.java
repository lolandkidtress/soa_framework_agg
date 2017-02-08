package com.James.Listeners;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.James.Invoker.RemoteInvoker;
import com.James.InvokerMonitor.InvokerStatus;
import com.James.basic.zkTools.iListeners;


/**
 * Created by James on 16/6/2.
 */
public class nodeReloadListenerImpl implements iListeners {

  private static final Log LOGGER = LogFactory.getLog(nodeReloadListenerImpl.class.getName());

  @Override
  public void Handle(String path, String type) {
    LOGGER.info(nodeReloadListenerImpl.class.getName() +"接收到"+ path +"事件:" + type);

    switch(type){
      //TODO 日志
    }

    RemoteInvoker remoteInvoker = (RemoteInvoker) InvokerStatus.getWatchedInvokers(path);

    remoteInvoker.create(path, remoteInvoker.getZkclient());
    LOGGER.info("reload Invoker完成");
  }

}
