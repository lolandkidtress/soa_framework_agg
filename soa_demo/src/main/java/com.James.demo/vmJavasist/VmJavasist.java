package com.James.demo.vmJavasist;

import com.James.demo.sample.Launch;
import com.James.soa_agent.HotInjecter;

/**
 * Created by James on 16/5/27.
 * 通过java agent的agentmain和javasist
 * 配合使用
 *
 * soa_agent需要作为jar文件在class目录中
 * 开发环境中需要将soa_agent项目关闭
 */
public class VmJavasist {
    public static void inject(){
//        AOP.getInstance().add_advice_method(Execution.class, new InfoGen_AOP_Handle_Execution());
        HotInjecter.getInstance().add_advice_method(Exc_annotation.class, new My_Agent_Handle());
        HotInjecter.getInstance().advice();
    }

    public static void main(String[] args) {
        inject();
        Launch launcher = new Launch();
        try{
            launcher.buildString(2);
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
