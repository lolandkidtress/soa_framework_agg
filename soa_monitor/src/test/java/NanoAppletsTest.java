import com.James.Annotation.tracking;
import com.James.MonitorHandle.trackingHandle;
import com.James.embeddedHttpServer.AppNanolets;
import com.James.soa_agent.HotInjecter;


/**
 * Created by James on 2017/2/6.
 */
public class NanoAppletsTest {    /**
 * Main entry point
 *
 * @param args
 */

public static void main(String[] args) throws Exception {
  HotInjecter.getInstance().add_advice_method(tracking.class, new trackingHandle());
  HotInjecter.getInstance().advice();

  new AppNanolets(9093);
  Thread.currentThread().join();
}
}
