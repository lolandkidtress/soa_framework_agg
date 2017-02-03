import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.avro.util.Utf8;

import com.James.avroNettyClientConnect.avroNettyClientConnection;
import com.James.avroNettyClientConnect.avroNettyClientConnectionManager;
import com.James.avroNettyClientConnect.avroNettyClientConnectionPool;
import com.James.avroNettyServer.avroServer;
import com.James.avroProto.Message;
import com.James.avroServiceRegist.avroRequestHandleRegister;
import com.James.basic.Model.sharedNode;
import com.James.basic.UtilsTools.CommonConfig;
import com.James.basic.UtilsTools.Return;


/**
 * Created by James on 2017/1/23.
 */
public class ConnectionPoolTest {

  public static void main(String args[]) throws IOException {

    //添加 test 的avroRPC接口
    avroRequestHandleRegister.INSTANCE.addRequestHandle("test", new sampleRequest());
    avroServer.startServer();
    //模拟一个节点
    sharedNode sn = new sharedNode();
    sn.setIP("192.168.21.218");
    sn.setRpc_port(CommonConfig.defaultAvroPort);
    //调用参数
    Message message = new Message();
    message.setParam(new Utf8(("setTo")));
    message.setRequestName(new Utf8("test"));

//    String response = new avroRpcClient().sendRequest("192.168.21.218",Integer.valueOf(CommonConfig.defaultAvroPort),message);
//    System.out.println("接受到请求返回" + response);

    avroNettyClientConnectionManager.getInstance().initConnectionPool(sn);
    avroNettyClientConnectionPool cp =
        avroNettyClientConnectionManager.getInstance().getConnectPool("192.168.21.218", CommonConfig.defaultAvroPort);

    ExecutorService mainExecutorService = Executors.newFixedThreadPool(50);

    Runnable r = new Runnable() {
      @Override
      public void run() {

      }
    };

    Callable<String> cb = new Callable() {
      @Override
      public Object call()
          throws Exception {
        avroNettyClientConnection conn = cp.getConnect();

        Return rt =conn.call(message);
        if(!rt.is_success()){
          System.out.print(rt.toJson());
        }
        cp.releaseConnect(conn);
        return null;
      }
    };

    List<Callable<String>> largeCallableList = new ArrayList<>();
    for(int i=0;i<50;i++){
      largeCallableList.add(cb);
    }

    List<Callable<String>> smallCallableList = new ArrayList<>();
    for(int i=0;i<20;i++){
      smallCallableList.add(cb);
    }
    try{
      while(true){

        mainExecutorService.invokeAll(smallCallableList);
        System.out.println("small任务结束");
        TimeUnit.SECONDS.sleep(10);
        mainExecutorService.invokeAll(largeCallableList);
        System.out.println("large任务结束");
        TimeUnit.SECONDS.sleep(10);

        System.out.println(cp.getConnSize().toString());
      }

    }catch (Exception e){
      e.printStackTrace();
    }

  }


}
