package com.James.embeddedHttpServer;

import java.util.Map;

import fi.iki.elonen.NanoHTTPD;


/**
 * Created by James on 16/8/5.
 * 监控类
 */
public class InvokeMonitor extends RouterNanoHTTPD.DefaultHandler{


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
      return "Error 404: \n the requested page doesn't exist.";
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

    if(targetUri.equals("/monitor/providers")) {
      //返回关注的服务提供者IP分布
      //TODO 节点分布
      //text = JsonConvert.toJson(com.James.Invoker.InvokerHelper.getInstance().getWatchedProvider());
//      String text = JsonConvert.toJson(InvokerMonitor.getInstance().watchedInvokers.get("sddf").getVersionedProviderInvokers());
      return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), text.toString());
    }

    if(targetUri.equals("/monitor/providerTreeMap")) {
      //返回关注的服务提供者权重分布情况
      //TODO 节点权重
      //text = JsonConvert.toJson(Invoker.getWatchedInvokers());
//      String text = JsonConvert.toJson(InvokerMonitor.getInstance().watchedInvokers.get("sddf").getVersionedProviderInvokers());
      return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), text.toString());
    }


    //404 not found
    return new Error404UriHandler().get(uriResource, urlParams, session);

  }
}
