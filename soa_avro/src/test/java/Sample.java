import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.avro.ipc.NettyServer;
import org.apache.avro.ipc.Server;
import org.apache.avro.ipc.specific.SpecificResponder;
import org.apache.avro.util.Utf8;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.James.avroNettyClientConnect.avroRpcClient;
import com.James.avroNettyServer.avroServer;
import com.James.avroProto.Message;
import com.James.avroProto.avrpRequestProto;
import com.James.avroServiceRegist.avroRequestHandleRegister;
import com.James.basic.UtilsTools.CommonConfig;
import com.James.basic.UtilsTools.JsonConvert;
import com.James.basic.UtilsTools.Parameter;


/**
 * Created by James on 16/6/26.
 */
public class Sample {
  private static final Log LOGGER = LogFactory.getLog(Sample.class.getName());

  public static class avrpRequestProtoImpl implements avrpRequestProto {
    // in this simple example just return details of the message
    public Utf8 send(Message message) {
      LOGGER.info("接收到" + message.getRequestName() + "请求");
      String ret ="";

      //从注册的处理器中找对应的
      Class avrpRequestProto = avroRequestHandleRegister.INSTANCE.getRequestHandle(
          message.getRequestName().toString());
//      avrpRequestProto avrpRequestProto =  avroRequestHandleRegister.INSTANCE.getRequestHandle(
//          message.getRequestName().toString());
      if(avrpRequestProto==null){
        return new Utf8("没有服务");
      }
      try{
        avrpRequestProto avrpRequestProtoImpl = (avrpRequestProto) avrpRequestProto.newInstance();
        ret =  avrpRequestProtoImpl.send(message).toString();
      }catch(Exception e){
        e.printStackTrace();
        LOGGER.error("调用avro接口异常",e);
      }

      return new Utf8(ret);
    }
  }

  private static Server server;

  public static void startServer() throws IOException {
    startServer(Integer.valueOf(CommonConfig.defaultAvroPort));
  }

  public static void startServer(int port) throws IOException {
    server = new NettyServer(new SpecificResponder(avrpRequestProto.class, new avrpRequestProtoImpl()), new InetSocketAddress(port));
    // the server implements the Mail protocol (MailImpl)
  }

  public static void main(String[] args) throws IOException {

    Parameter par = new Parameter().add("setTo","v_setTo");

    Message message = new Message();
    message.setParam(JsonConvert.toJson(par));
    message.setRequestName(new Utf8("test"));

//    avroRequestHandleRegister.INSTANCE.addRequestHandle("test", new sampleRequest());
    avroRequestHandleRegister.INSTANCE.addRequestHandle("test", sampleRequest.class);
    avroServer.startServer();
    avroRpcClient client = new avroRpcClient();

    String response = client.sendRequest("127.0.0.1",Integer.valueOf(CommonConfig.defaultAvroPort),message);
    System.out.println("接受到请求返回" + response);
    response = client.sendRequest("127.0.0.1",Integer.valueOf(CommonConfig.defaultAvroPort),message);
    System.out.println("接受到请求返回" + response);
    response = client.sendRequest("127.0.0.1",Integer.valueOf(CommonConfig.defaultAvroPort),message);
    System.out.println("接受到请求返回" + response);
    response = client.sendRequest("127.0.0.1",Integer.valueOf(CommonConfig.defaultAvroPort),message);
    System.out.println("接受到请求返回" + response);
    //Thread.currentThread().join();
    System.exit(0);
  }
}
