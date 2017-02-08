package com.James.JettyServer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;


/**
 * Created by James on 16/8/10.
 */
@WebListener
public class Init_Listener implements ServletContextListener {

  /*
   * (non-Javadoc)l
   *
   * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
   */
  @Override
  public void contextInitialized(ServletContextEvent event) {
    event.getServletContext().log("服务启动 ...");
    try {
      event.getServletContext().log("加载配置文件");

    } catch (Exception e) {
      e.printStackTrace();
      event.getServletContext().log("服务启动失败");
      System.exit(1);
    }
  }

  @Override
  public void contextDestroyed(ServletContextEvent event) {
    event.getServletContext().log("服务结束");
  }
}
