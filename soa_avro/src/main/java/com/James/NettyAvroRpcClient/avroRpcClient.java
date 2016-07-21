package com.James.NettyAvroRpcClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.avro.AvroRemoteException;
import org.apache.avro.ipc.NettyTransceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;
import org.apache.avro.util.Utf8;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.James.avroProto.Message;
import com.James.avroProto.avrpRequestProto;
import com.James.avroServiceRegist.avroRequestHandleRegister;
import com.James.basic.Enum.Code;
import com.James.basic.UtilsTools.JsonConvert;
import com.James.basic.UtilsTools.Return;


/**
 * Created by James on 16/6/23.
 */
public class avroRpcClient {
  private static final Logger LOGGER = LoggerFactory.getLogger(avroRpcClient.class.getName());

  private static class InnerInstance {
    public static final avroRpcClient instance = new avroRpcClient();
  }

  public static avroRpcClient getInstance() {
    return InnerInstance.instance;
  }

  private ExecutorService callTimeoutPool;
  private final ReentrantLock stateLock = new ReentrantLock();

  private ConcurrentHashMap<String,NettyTransceiver> registClient =new ConcurrentHashMap<>();

  private static class TransceiverThreadFactory implements ThreadFactory {
    private final AtomicInteger threadId = new AtomicInteger(0);
    private final String prefix;

    /**
     * Creates a TransceiverThreadFactory that creates threads with the
     * specified name.
     * @param prefix the name prefix to use for all threads created by this
     * ThreadFactory.  A unique ID will be appended to this prefix to form the
     * final thread name.
     */
    public TransceiverThreadFactory(String prefix) {
      this.prefix = prefix;
    }

    @Override
    public Thread newThread(Runnable r) {
      Thread thread = new Thread(r);
      thread.setDaemon(true);
      thread.setName(prefix + " " + threadId.incrementAndGet());
      return thread;
    }
  }

  //Impl实现
  public static class avrpRequestProtoImpl implements avrpRequestProto {

    public Utf8 send(Message message) {
      LOGGER.info("接收到" + message.getRequestName() + "请求");
      String ret ="";

      avrpRequestProto avrpRequestProto =  avroRequestHandleRegister.INSTANCE.getRequestHandle(
          message.getRequestName().toString());
      if(avrpRequestProto==null){
        return new Utf8("没有服务");
      }
      try{
        ret = avrpRequestProto.send(message).toString();
      }catch(AvroRemoteException e){
        e.printStackTrace();
        LOGGER.error("调用avro接口异常",e);
      }

      return new Utf8(ret);
    }
  }

  //TODO 连接池实现
  public static NettyTransceiver create(String hostname,int port,boolean keepAlive) throws IOException {

    Map<String, Object> options = new HashMap<String, Object>(3);
    options.put(NettyTransceiver.NETTY_TCP_NODELAY_OPTION, NettyTransceiver.DEFAULT_TCP_NODELAY_VALUE);
    options.put("keepAlive", keepAlive);
    options.put(NettyTransceiver.NETTY_CONNECT_TIMEOUT_OPTION, NettyTransceiver.DEFAULT_CONNECTION_TIMEOUT_MILLIS);

    return new NettyTransceiver(new InetSocketAddress(hostname,port), new NioClientSocketChannelFactory(
        Executors.newCachedThreadPool(new DaemonThreadFactory(new TransceiverThreadFactory("avro-client-boss"))),
        Executors.newCachedThreadPool(new DaemonThreadFactory(new TransceiverThreadFactory("avro-client-worker")))),
        options);
  }


  public static Return call(String hostname,int port,Message message){
    NettyTransceiver client ;

    try{
      client = new NettyTransceiver(new InetSocketAddress(hostname,port));
      avrpRequestProto proxy = (avrpRequestProto) SpecificRequestor.getClient(avrpRequestProto.class, client);
      LOGGER.info("avro client初始化成功");
//      Message message = new Message();
//      message.setRequestName("test");
//      message.setParam("{\"\":\"\"}");

      String response = proxy.send(message).toString();
      if(client!=null && client.isConnected()){
        client.close();
      }

      return JsonConvert.toObject(response,Return.class);

    }catch(IOException e){
      e.printStackTrace();
      LOGGER.error("调用nettyavro异常",e);
      return Return.FAIL(Code.error.code,Code.error.name());
    }

  }

  public String sendRequest(String hostname,int port,Message message){
    try{

      return bindProxy(initClient(hostname,port)).send(message).toString();
    }catch(AvroRemoteException e){
      e.printStackTrace();
      LOGGER.error("调用" + message.getRequestName() + "接口异常",e);
      return null;
    }

  }

  private avrpRequestProto bindProxy(NettyTransceiver client){
    try{
      avrpRequestProto proxy = (avrpRequestProto) SpecificRequestor.getClient(avrpRequestProto.class, client);

      return proxy;
    }catch(IOException e) {
      e.printStackTrace();
      LOGGER.error("绑定request接口异常",e);
      return null;
    }

  }

  private NettyTransceiver initClient(String hostname,int port){
    try{
      NettyTransceiver client = new NettyTransceiver(new InetSocketAddress(hostname,port));
      return client;
    }catch (IOException e){
      e.printStackTrace();
      LOGGER.error("初始化netty client 异常",e);
      return null;
    }

  }




  private static class DaemonThreadFactory implements ThreadFactory {
    private ThreadFactory delegate;

    DaemonThreadFactory() {
      this.delegate = Executors.defaultThreadFactory();
    }

    DaemonThreadFactory(ThreadFactory delegate) {
      this.delegate = delegate;
    }

    @Override
    public Thread newThread(Runnable r) {
      Thread thread = delegate.newThread(r);
      // Using daemon threads so that client applications would exit without having to properly
      // close the RemoteRepository.
      thread.setDaemon(true);
      return thread;
    }
  }


}
