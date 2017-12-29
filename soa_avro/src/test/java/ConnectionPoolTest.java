import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.avro.util.Utf8;

import com.James.avroNettyClientConnect.avroNettyClientConnection;
import com.James.avroNettyClientConnect.avroNettyClientConnectionManager;
import com.James.avroNettyClientConnect.avroNettyClientConnectionPool;
import com.James.avroNettyServer.avroServer;
import com.James.avroProto.Message;
import com.James.avroServiceRegist.avroRequestHandleRegister;
import com.James.basic.Model.SharedNode;
import com.James.basic.UtilsTools.CommonConfig;
import com.James.basic.UtilsTools.Return;


/**
 * Created by James on 2017/1/23.
 */
public class ConnectionPoolTest {

  public static void main(String args[]) throws Exception {

    //添加 test 的avroRPC接口
    avroRequestHandleRegister.INSTANCE.addRequestHandle("test", sampleRequest.class);
    avroServer.startServer();
    //模拟一个节点
    SharedNode sn = new SharedNode();
    sn.setIP("127.0.0.1");
    sn.setRpc_port(CommonConfig.defaultAvroPort);
    //调用参数
    Message message = new Message();
    message.setParam(new Utf8(("setTo")));
    message.setRequestName(new Utf8("test"));

//    String response = new avroRpcClient().sendRequest("192.168.21.218",Integer.valueOf(CommonConfig.defaultAvroPort),message);
//    System.out.println("接受到请求返回" + response);

    avroNettyClientConnectionManager.getInstance().initConnectionPool(sn);
    avroNettyClientConnectionPool cp =
        avroNettyClientConnectionManager.getInstance().getConnectPool("127.0.0.1", CommonConfig.defaultAvroPort);

    ExecutorService mainExecutorService = Executors.newFixedThreadPool(100);

    Runnable r = new Runnable() {
      @Override
      public void run() {

      }
    };

    Callable<Boolean> cb = new Callable() {
      @Override
      public Boolean call()
          throws Exception {

        try{
          avroNettyClientConnection conn = cp.getConnect();
          Return rt =conn.call(message);
          cp.releaseConnect(conn);
          return true;
        }catch(Exception e){
          e.printStackTrace();
          return false;
        }
      }
    };

    List<Callable<Boolean>> largeCallableList = new ArrayList<>();
    for(int i=0;i<100;i++){
      largeCallableList.add(cb);
    }

    List<Callable<Boolean>> smallCallableList = new ArrayList<>();
    for(int i=0;i<20;i++){
      smallCallableList.add(cb);
    }
    try{

      avroNettyClientConnection conn = cp.getConnect();
      Return rt =conn.call(message);
      cp.releaseConnect(conn);
//      while(true){
//
//        List<Future<Boolean>> sft = mainExecutorService.invokeAll(smallCallableList);
//
//        System.out.println("small执行成功:" + sft.stream().filter(f -> {
//                    try {
//                      if (f.get() == true) {
//                        return true;
//                      } else {
//                        return false;
//                      }
//                    } catch (Exception e) {
//                      return false;
//                    }
//                  }).count());
//
//
//        System.out.println("small任务结束");
//        TimeUnit.SECONDS.sleep(10);
//        List<Future<Boolean>> lft = mainExecutorService.invokeAll(largeCallableList);
//
//        System.out.println("large执行成功:" + lft.stream().filter(f -> {
//          try {
//            if (f.get() == true) {
//              return true;
//            } else {
//              return false;
//            }
//          } catch (Exception e) {
//            return false;
//          }
//        }).count());
//
//        System.out.println("large任务结束");
//        TimeUnit.SECONDS.sleep(10);
//
//        System.out.println(cp.getConnSize().get("currentAvail"));
//      }

    }catch (Exception e){
      e.printStackTrace();
    }
  }

}
