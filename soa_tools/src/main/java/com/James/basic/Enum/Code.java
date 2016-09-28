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
    method_notsupport(300100,"不支持的请求方法"),
    protocol_notsupport(300200,"不支持的协议"),
    node_unavailable(300300, "没有可用的服务节点"), //

    //40000 异常
    error(400100, "执行错误"), //
    service_notfound(400200, "没有这个服务"), //
    service_degraded(400300, "服务降级"), //

    //50000 流程控制错误
    over_limit(500100, "接口调用次数超过限制");



    public String note;
    public Integer code;

    private Code(Integer code, String note) {
        this.note = note;
        this.code = code;
    }
}
