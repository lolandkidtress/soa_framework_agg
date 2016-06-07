package com.James.http_callback;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by James on 16/6/7.
 */
public class Http_Callback<T> {
  private static final Logger LOGGER = LoggerFactory.getLogger(Http_Callback.class.getName());

  private ArrayBlockingQueue<T> queue = new ArrayBlockingQueue<T>(1);

  public T get(Integer seconds) {
    try {
      return queue.poll(seconds, TimeUnit.SECONDS);
    } catch (Exception e) {
      LOGGER.error("获取异步返回值异常", e);
    }
    return null;
  }

  public void run(T value) {
    if (value == null) {
      LOGGER.warn("参数不能为空");
      return;
    }
    queue.add(value);
  }
}
