package com.James.Listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.James.Invoker.Invoker;
import com.James.Invoker.InvokerHelper;


/**
 * Created by James on 16/6/2.
 */
public class dataChangedListener implements iListeners{

  private static final Logger LOGGER = LoggerFactory.getLogger(dataChangedListener.class.getName());


  @Override
  public void DataChanged(String path, String type) {
    LOGGER.info(path + "接收到"+type+"事件");

//    switch (type) {
//
//      //TODO
//      case "" :
//
//    }

    Invoker invoker = InvokerHelper.getInstance().getWatchedInvokers(path);
    invoker.watchZKDataChange(path);
  }

  @Override
  public void ConnectionRecover(String path, String type) {
    LOGGER.info(path + "接收到"+type+"事件");

    switch (type) {

      //TODO
      case "" :

    }
    Invoker invoker = InvokerHelper.getInstance().getWatchedInvokers(path);
    invoker.watchZKConnectStat(path);
  }

  @Override
  public void ConnectionLost(String path, String type) {
    LOGGER.info(path + "接收到"+type+"事件");

    switch (type) {

      //TODO
      case "" :

    }
    Invoker invoker = InvokerHelper.getInstance().getWatchedInvokers(path);
    invoker.watchZKConnectStat(path);
  }

  @Override
  public void ChildChanged(String path, String type) {
    LOGGER.info(path + "接收到"+type+"事件");

    switch (type) {

      //TODO
      case "" :

    }
    Invoker invoker = InvokerHelper.getInstance().getWatchedInvokers(path);
    invoker.watchZKChildChange(path);
  }
}
