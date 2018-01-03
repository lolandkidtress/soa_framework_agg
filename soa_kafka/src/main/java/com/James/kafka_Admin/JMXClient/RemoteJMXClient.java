package com.James.kafka_Admin.JMXClient;

import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;


/**
 * Created by James on 2018/1/2.
 */
public class RemoteJMXClient {

  private MBeanServerConnection mbsc ;

  public void createMBeanServerConnect(String ip,
      String jmxport) {
    try {
      String jmxURL = "service:jmx:rmi:///jndi/rmi://" + ip + ":"
          + jmxport + "/jmxrmi";
      // jmx
      // url
      JMXServiceURL serviceURL = new JMXServiceURL(jmxURL);

//      Map map = new HashMap();
//      String[] credentials = new String[] { userName, password };
//      map.put("jmx.remote.credentials", credentials);
//      JMXConnector connector = JMXConnectorFactory.connect(serviceURL, map);
      JMXConnector connector = JMXConnectorFactory.connect(serviceURL);
      mbsc = connector.getMBeanServerConnection();


    } catch (Exception e) {
      e.printStackTrace();


    }

  }

  public void getObject() {
    try {
      Set<ObjectName> beanSet = mbsc.queryNames(null, null);
      System.out.println(beanSet);
    } catch (Exception e) {
      e.printStackTrace();

    }
  }

  public static void main(String[] args) {
    RemoteJMXClient client = new RemoteJMXClient();
    client.createMBeanServerConnect("10.81.23.100","9999");
    client.getObject();
  }
}
