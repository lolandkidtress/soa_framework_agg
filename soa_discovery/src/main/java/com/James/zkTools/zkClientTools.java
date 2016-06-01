package com.James.zkTools;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.curator.CuratorZookeeperClient;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.api.transaction.CuratorTransaction;
import org.apache.curator.framework.api.transaction.CuratorTransactionFinal;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.retry.RetryOneTime;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by James on 16/4/7.
 * 常用ZK的Api
 *
 */
public class zkClientTools {
    public static final Logger LOGGER = LoggerFactory.getLogger(zkClientTools.class.getName());

    public static Charset charset = StandardCharsets.UTF_8;

    private String connectString;
    //private String namespace = "/NAME";
    private int timeout = 30000;
    private int retry = 5;
    private int retryDuration = 1000;

    private CuratorFramework zkTools;

    public zkClientTools() {

    }

    public zkClientTools(String connectString){
        this.connectString = connectString;
        initCuratorClient(connectString);

    }

    public static boolean isConnected(String connectString){
        //低级Api,不推荐使用
        CuratorZookeeperClient client = new CuratorZookeeperClient(
                connectString, 10000, 10000, null, new RetryOneTime(1));
        try{
            client.start();
            client.blockUntilConnectedOrTimedOut();

            if(client.isConnected()){
                client.close();
                return true;
            }else{
                client.close();
                return false;
            }

        }catch(Exception e){
            LOGGER.error(e.getMessage());
            if(client.isConnected()){
                client.close();
            }
            return false;
        }

    }

    public void initCuratorClient(String connectString){

            this.zkTools = CuratorFrameworkFactory
                    .builder()
                    .connectString(connectString)
                            //.namespace(namespace)
                    .retryPolicy(new RetryNTimes(retry,retryDuration))
                    .connectionTimeoutMs(timeout)
                    .build();
            this.zkTools.start();

    }

    public void createEPHEMERALNode(String Path) throws Exception{
        zkTools.create()   //创建路径
                .creatingParentsIfNeeded()   //父节点不存在，递归创建
                .withMode(CreateMode.EPHEMERAL)  //临时节点
                .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)  //权限
                .forPath(Path);
    }

    public void createPERSISTENTNode(String Path) throws Exception{
        zkTools.create()   //创建路径
                .creatingParentsIfNeeded()   //父节点不存在，递归创建
                .withMode(CreateMode.PERSISTENT)  //永久节点
                .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)  //权限
                .forPath(Path);
    }

    public List<String> getChildren(String Path) throws Exception{

        return zkTools.getChildren()
                .forPath(Path);
    }

    public String getContent(String Path) throws Exception{
        byte[] bytes = zkTools.getData()
                .forPath(Path);
        if(bytes!=null){
            return new String(bytes, charset);
        }else{
            return "";
        }

    }

    public void setContent(String Path,String Content) throws Exception{
        zkTools.setData()
                .forPath(Path, Content.getBytes(charset.name()));

    }

    public static CuratorTransaction startTransaction(CuratorFramework client)
    {
        // start the transaction builder
        return client.inTransaction();
    }

    public static void commitTransaction(CuratorTransactionFinal client) throws Exception
    {
        // commit the transaction
        client.commit();
    }

    public String watchedData(CuratorFramework client, String path,CuratorListener listener) throws Exception{

        //getting event/async notifications
        client.getCuratorListenable().addListener(listener);

        byte[] bytes = zkTools.getData().watched().forPath(path);
        if(bytes!=null){
            return new String(bytes,StandardCharsets.UTF_8);
        }else{
            return "";
        }
    }

    public void watchConnectStat(CuratorFramework client, String path,ConnectionStateListener listener) throws Exception{

        //getting event/async notifications
        client.getConnectionStateListenable().addListener(listener);

    }

    public boolean checkExists(String Path) throws Exception {

        if(zkTools.checkExists().forPath(Path) !=null){
            return true;
        }else{
            return false;
        }

    }

    public void guaranteeddeleteNode(String Path) throws Exception {
        zkTools.delete().guaranteed().forPath(Path);

    }

    public CuratorFramework getCuratorFramework(){
        return zkTools;
    }

    public void setCuratorFramework(CuratorFramework client){
        this.zkTools = client;
    }
}
