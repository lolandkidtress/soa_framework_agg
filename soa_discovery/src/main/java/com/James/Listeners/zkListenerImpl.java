package com.James.Listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.James.Invoker.Invoker;
import com.James.Invoker.InvokerHelper;


/**
 * Created by James on 16/6/2.
 */
public class zkListenerImpl implements iListeners{

  private static final Logger LOGGER = LoggerFactory.getLogger(zkListenerImpl.class.getName());

  @Override
  public void Handle(String path, String type) {
    LOGGER.info(zkListenerImpl.class.getName() +"接收到"+ path +"事件:" + type);

    switch(type){

    }

    Invoker invoker = InvokerHelper.getInstance().getWatchedInvokers(path);

    invoker.create(path,invoker.getZkclient());
  }

}
