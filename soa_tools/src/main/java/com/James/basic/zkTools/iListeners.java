package com.James.basic.zkTools;

/**
 * Created by James on 16/5/27.
 *
 */
public interface iListeners {
//    public void DataChanged(String path,String type);
//    public void ConnectionRecover(String path,String type);
//    public void ConnectionLost(String path,String type);
//    public void ChildChanged(String path,String type);

    public void Handle(String path, String type);

//    public void watchDataChanged(String path,String type);
}
