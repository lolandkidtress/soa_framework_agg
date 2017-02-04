package com.James.basic.Enum;

/**
 * Created by James on 16/5/23.
 */
public enum Code {
    //10000 成功
    success(10200, "成功"), //

    //20000 参数方法返回值错误
    parameters_incorrect(200100, "参数不正确"), //
    generate_return_error(200101, "处理返回值错误"), //

    //30000 不支持的方法协议,服务不可用
    method_not_support(300100,"不支持的请求方法"),
    protocol_not_support(300200,"不支持的协议"),
    node_unavailable(300300, "没有可用的服务节点"), //

    //50000 异常
    error(500100, "系统异常"), //
    service_not_found(500200, "没有这个服务"), //
    over_limit(500300, "接口调用次数超过限制") ,//
    service_degrade(500400, "接口调用次数超过限制") ,
    avro_Connection_not_available(500500,"avro连接不可用"),
    avro_Connection_Max_limit(500600,"avro连接无法初始化,已达上限");


    public String note;
    public Integer code;

    private Code(Integer code, String note) {
        this.note = note;
        this.code = code;
    }

    public Integer getCode(){
        return this.code;
    }

    public String getNote(){
        return this.note;
    }
}
