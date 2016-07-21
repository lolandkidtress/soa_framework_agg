package com.James.Invoker;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.James.basic.UtilsTools.JsonConvert;
import com.James.embeddedHttpServer.RouterNanoHTTPD;

import fi.iki.elonen.NanoHTTPD;


/**
 * Created by James on 16/6/2.
 * 服务调用辅助
 */
public class InvokerHelper extends RouterNanoHTTPD.DefaultHandler {


  private static class InnerInstance {
    public static final InvokerHelper instance = new InvokerHelper();
  }

  public static InvokerHelper getInstance() {
    return InnerInstance.instance;
  }


  //关注的服务提供者
  private ConcurrentHashMap<String,Invoker> watchedInvokers = new ConcurrentHashMap();

  public Invoker getWatchedInvokers(String key){
    return watchedInvokers.get(key);
  }

  public void setWatchedInvokers(String key,Invoker invoker){
    this.watchedInvokers.put(key,invoker);
  }


  @Override
  public String getText() {
    return "not implemented";
//        return GetStatus();
  }

  @Override
  public String getMimeType() {
    return "application/json;charset=utf-8";
  }

  @Override
  public NanoHTTPD.Response.IStatus getStatus() {
    return NanoHTTPD.Response.Status.OK;
  }

  public static class Error404UriHandler extends RouterNanoHTTPD.DefaultHandler {

    public String getText() {
      return "<html><body><h3>Error 404: the requested page doesn't exist.</h3></body></html>";
    }

    @Override
    public String getMimeType() {
      return "text/html";
    }

    @Override
    public NanoHTTPD.Response.IStatus getStatus() {
      return NanoHTTPD.Response.Status.NOT_FOUND;
    }
  }

  public NanoHTTPD.Response get(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {

    String targetUri = session.getUri();

//        final HashMap<String, String> map = new HashMap<String, String>();
//        try{
//            session.parseBody(map);
//            final String json = map.get("postData");
//            System.out.println(json);
//        }catch(Exception e){
//            e.printStackTrace();
//        }
    if(targetUri.equals("/monitor/Status")) {
      //返回关注的服务提供者的信息
      String text = JsonConvert.toJson(watchedInvokers);
      return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), text.toString());

    }


    //404 not found
    return new Error404UriHandler().get(uriResource, urlParams, session);

  }


}
