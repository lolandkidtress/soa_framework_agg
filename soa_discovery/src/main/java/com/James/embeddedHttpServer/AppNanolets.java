package com.James.embeddedHttpServer;

import java.io.IOException;

import com.James.Invoker.InvokeMonitor;
import com.James.basic.UtilsTools.CommonConfig;


/**
 * Created by James on 16/4/29.
 */
public class AppNanolets extends RouterNanoHTTPD {

    //default
    private static int PORT = Integer.valueOf(CommonConfig.nanoHttpPort);

    /**
     * Create the server instance
     */
    public AppNanolets(int Port) throws IOException {
        super(Port);
        addMappings();
        start();
        System.out.println("\nRunning! Point your browers to http://localhost:" + Port + "/ \n");
    }

    public AppNanolets() throws IOException {
        super(PORT);
        addMappings();
        start();
        System.out.println("\nRunning! Point your browers to http://localhost:" + PORT + "/ \n");
    }

    /**
     * Add the routes Every route is an absolute path Parameters starts with ":"
     * Handler class should implement @UriResponder interface If the handler not
     * implement UriResponder interface - toString() is used
     */
    @Override
    public void addMappings() {
        super.addMappings();
        addRoute("/monitor/providers", InvokeMonitor.class);
        addRoute("/monitor/providerTreeMap", InvokeMonitor.class);
//        addRoute("/user/:id", UserHandler.class);
//        addRoute("/user/help", GeneralHandler.class);
//        addRoute("/general/:param1/:param2", GeneralHandler.class);
//        addRoute("/photos/:customer_id/:photo_id", null);
//        addRoute("/test", String.class);
//        addRoute("/interface", UriResponder.class); // this will cause an error
//        // when called
//        addRoute("/toBeDeleted", String.class);
//        removeRoute("/toBeDeleted");
//        addRoute("/stream", StreamUrl.class);
//        addRoute("/browse/(.)+", StaticPageTestHandler.class, new File("src/test/resources").getAbsoluteFile());
    }

    /**
     * Main entry point
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {
        new AppNanolets(9093);

        Thread.currentThread().join();


//        ServerRunner.run(AppNanolets.class);
    }
}