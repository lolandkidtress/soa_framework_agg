package com.James.demo.providerRegister;

import com.James.Provider.providerInstance;


/**
 * Created by James on 16/6/1.
 */
public class Register {

    public void start(){
      String zkconnect = "172.16.8.98:2181";
      providerInstance.getInstance().startServer(zkconnect);
    }

  public static void main(String[] args) {
    new Register().start();
  }

}
