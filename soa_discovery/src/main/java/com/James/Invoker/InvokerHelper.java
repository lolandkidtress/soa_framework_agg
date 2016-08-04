package com.James.Invoker;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import com.James.Model.providerInvoker;
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

  public InvokerHelper(){
      getInstance();
  }

  public static InvokerHelper getInstance() {
    return InnerInstance.instance;
  }


  //关注的服务提供者实体
  private ConcurrentHashMap<String,Invoker> watchedInvokers = new ConcurrentHashMap();

  public Invoker getWatchedInvokers(String key){
    return watchedInvokers.get(key);
  }

  public void setWatchedInvokers(String key,Invoker invoker){
    this.watchedInvokers.put(key,invoker);
  }

  //关注的服务提供者描述
  private ConcurrentHashMap<String,String> watchedInvokersDescription = new ConcurrentHashMap();

  public ConcurrentHashMap<String, String> getWatchedInvokersDescription() {
    return watchedInvokersDescription;
  }

  public void setWatchedInvokersDescription(ConcurrentHashMap<String, String> watchedInvokersDescription) {
    this.watchedInvokersDescription = watchedInvokersDescription;
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
      return "application/json";
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
    String text ="";

    if(targetUri.equals("/monitor/invokers")) {

    }

    if(targetUri.equals("/monitor/invokersDetail")) {
      //返回关注的服务提供者权重分布情况

      ConcurrentHashMap<String,Invoker> p_watchedInvokers = InvokerHelper.getInstance().watchedInvokers;
      Iterator<String> it_key = p_watchedInvokers.keySet().iterator();

      while(it_key.hasNext()){
        String providerName = it_key.next();

        Invoker invoker = p_watchedInvokers.get(providerName);
        text = text.concat("provider:" + providerName + "\n");
        ConcurrentHashMap<String, providerInvoker> versionedProviderInvokers = invoker.getVersionedProviderInvokers();
        Iterator<String> it_ver = versionedProviderInvokers.keySet().iterator();

        while(it_ver.hasNext()){
          String ver = it_ver.next();
          text = text.concat("version:" + ver + "\n");
          ConcurrentHashMap<String, TreeMap> tree_Map = versionedProviderInvokers.get(ver).getMethodTreeMapNodes();
          text = text.concat("\n");
          text = text.concat(JsonConvert.toJson(tree_Map));

        }

        text = text.concat("\n\n\n");
      }

//      String text = JsonConvert.toJson(InvokerHelper.getInstance().watchedInvokers.get("sddf").getVersionedProviderInvokers());
      return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), text.toString());
    }


    //404 not found
    return new Error404UriHandler().get(uriResource, urlParams, session);

  }


}
