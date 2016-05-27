package com.James.soa_agent;

/**
 * Created by James on 16/5/25.
 * 取得agent jar包的绝对位置
 */
public class Agent_LocalPath {
    // file:/home/xxx/.m2/repository/com/soa_agent/V1.0.00R150210/soa_agent-V1.0.00R150210.jar
    // file:/home/xxx/workspace/infogen/lib/soa_agent.jar!xxxx.class
    public static String path() {
        String location = Agent_LocalPath.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        location = location.replace("file:", "");
        if (System.getProperty("os.name").indexOf("Windows") != -1) {
            location = location.substring(1);
        }
        if (location.contains(".jar!")) {
            location = location.substring(0, location.indexOf(".jar!")).concat(".jar");
        }
        return location;
    }
}
