import org.apache.avro.util.Utf8;

import com.James.avroProto.Message;
import com.James.avroProto.avrpRequestProto;


/**
 * Created by James on 16/6/26.
 */
public class sampleRequest implements avrpRequestProto {
  private String classname = this.getClass().getName();
  static{
    System.out.println("test init" );

  }

  public sampleRequest(){

  }

  @Override
  public Utf8 send(Message message){
    System.out.println("取得的参数为:" + message.getParam() );
    return new Utf8("call test");
  }
}
