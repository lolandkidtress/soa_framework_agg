package com.James.Model;

import java.sql.Timestamp;
import java.time.Clock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.James.Provider.providerInstance;
import com.James.basic.UtilsTools.CommonConfig;
import com.James.basic.UtilsTools.Utils;
import com.fasterxml.jackson.annotation.JsonAutoDetect;


/**
 * Created by James on 16/5/30.
 * 服务提供方
 * 向zk注册自身的信息
 *
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class SharedProvider {

  protected String author;
  //服务名称
  //http下应该对应request_mapping
  //rpc下应该是class的全限定名称
  protected String method_name ;
  //节点名称
  protected String server_name;

  protected String version= CommonConfig.DEFAULTVERSION;

  //协议 http,avro,thrift等
  protected CommonConfig.PROTOCOL protocol = CommonConfig.PROTOCOL.http;

  //server_name+Method_name+protocol+version + ip 组成一个唯一标示
  protected String identityID;

  protected String describe = "";
  protected Integer min_nodes = 1;

  protected String ip="";
  protected String net_ip="";

  protected String http_port="";
  protected String rpc_port="";

  protected String submit_mode="";  //get,post

  //容器的context,tomcat应该配置
  protected String http_context="";

  //入参
  private List<InputParam> inputParams = new ArrayList<>();
  //出参
  private List<OutputParam> outputParams = new ArrayList<>();

  // 其他个性化配置
  protected Map<String, Object> metadata = new HashMap<>();
  // 不需要配置
  protected Timestamp time = new Timestamp(Clock.system(CommonConfig.ZONDID) .millis());


  public SharedProvider(){

    this.server_name = providerInstance.getInstance().getServerName();
    this.ip=Utils.getLocalIP();
  }

  public String getAuthor() {
    return author;
  }
  public void setAuthor(String Author) {
    this.author = Author;
  }

  public String getMethod_name() {
    return method_name;
  }
  public void setMethod_name(String Method_name) {
    this.method_name = Method_name;
  }

  public String getServer_name() {
    return server_name;
  }
  public void setServer_name(String Server_name) {
    this.server_name = Server_name;
  }

  public String getVersion() {
    return version;
  }
  public void setVersion(String Version) {
    this.version = Version;
  }

  public CommonConfig.PROTOCOL getProtocol() {
    return protocol;
  }
  public void setProtocol(CommonConfig.PROTOCOL Protocol) {
    this.protocol = Protocol;
  }

  public String getIdentityID(){
    return this.identityID;
  }

  public String getDescribe(){
    return this.describe;
  }

  public void setDescribe(String Describe){
    this.describe = Describe;
  }

  public String getIP(){
    return this.ip;
  }
  public void setIP(String IP){
    this.ip= IP;
  }

  public String getNet_ip(){
    return this.net_ip;
  }
  public void setNet_ip(String Net_ip){
    this.net_ip=Net_ip;
  }

  public String getHttp_port(){
    return this.http_port;
  }
  public void setHttp_port(String Http_port){
    this.http_port = Http_port;
  }

  public String getRpc_port(){
    return this.rpc_port;
  }
  public void setRpc_port(String Rpc_port){
    this.rpc_port=Rpc_port;
  }

  public String getSubmit_mode(){
    return this.submit_mode;
  }
  public void setSubmit_mode(String Submit_mode){
    this.submit_mode = Submit_mode;
  }

  public List getInputParams(){
    return this.inputParams;
  }
  public void setInputParams(List InputParams){
    this.inputParams = InputParams;
  }
  public void addInputParam(InputParam inputParam){
    this.inputParams.add(inputParam);
  }


  public List getOutputParams(){
    return this.outputParams;
  }
  public void setOutputParams(List OutputParams){
    this.outputParams = OutputParams;
  }
  public void addOutputParam(OutputParam outputParam){
    this.outputParams.add(outputParam);
  }


  public String getHttp_context(){
    return this.http_context;
  }
  public void setHttp_context(String Http_context){
    this.http_context=Http_context;
  }


  public Map<String,Object> getMetadata(){
    return this.metadata;
  }
  public void setMetadata(Map<String,Object> Metadata){
    this.metadata=Metadata;
  }

  public boolean isAvailable(){
    if(this.server_name!=null&&this.server_name.length()>0 &&
        this.method_name!=null&&this.method_name.length() >0 &&
        this.ip!=null&&this.ip.length()>0 &&
        this.protocol!=null&&this.protocol.name().length()>0 &&
        this.version!=null&&this.version.length()>0
        )
    {
      this.identityID = this.server_name.concat(CommonConfig.HYPHEN)
          .concat(this.method_name).concat(CommonConfig.HYPHEN)
          .concat(this.protocol.name()).concat(CommonConfig.HYPHEN)
          .concat(this.version).concat(CommonConfig.HYPHEN)
          .concat(this.ip);
      return true;

    }else{
      return false;
    }

  }

  public boolean isDefaultVersion(){
    if(this.version == CommonConfig.DEFAULTVERSION){
      return true;
    }
    return false;
  }


}
