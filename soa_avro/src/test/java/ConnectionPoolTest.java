import java.io.IOException;

import org.apache.avro.util.Utf8;

import com.James.avroNettyClientConnect.avroNettyClientConnection;
import com.James.avroNettyClientConnect.avroNettyClientConnectionManager;
import com.James.avroNettyClientConnect.avroNettyClientConnectionPool;
import com.James.avroNettyServer.avroServer;
import com.James.avroProto.Message;
import com.James.avroServiceRegist.avroRequestHandleRegister;
import com.James.basic.Model.sharedNode;
import com.James.basic.UtilsTools.CommonConfig;


/**
 * Created by James on 2017/1/23.
 */
public class ConnectionPoolTest {

  public static void main(String args[]) throws IOException {

    avroRequestHandleRegister.INSTANCE.addRequestHandle("test", new sampleRequest());
    avroServer.startServer();

    sharedNode sn = new sharedNode();
    sn.setIP("192.168.21.218");
    sn.setRpc_port(CommonConfig.defaultAvroPort);

    Message message = new Message();
    message.setParam(new Utf8(("setTo")));
    message.setRequestName(new Utf8("test"));

//    String response = new avroRpcClient().sendRequest("192.168.21.218",Integer.valueOf(CommonConfig.defaultAvroPort),message);
//    System.out.println("接受到请求返回" + response);

    avroNettyClientConnectionManager.getInstance().initConnectionPool(sn);
    avroNettyClientConnectionPool cp = avroNettyClientConnectionManager.getInstance().getConnectPool("192.168.21.218",
        CommonConfig.defaultAvroPort);
    while(true) {
      avroNettyClientConnection conn = cp.getConnect();
      conn.getName();
      conn.call(message);
      cp.releaseConnect(conn.getName());
    }
  }
}
