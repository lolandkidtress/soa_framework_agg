package com.James.basic.UtilsTools;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



/**
 * Created by James on 16/5/23.
 */
public class Utils {


    private static final Log LOGGER = LogFactory.getLog(Utils.class.getName());
    /**
     * @param e
     *            异常类
     * @return 拼接打印 exception 栈内容
     */
    public static String stacktrace(Throwable e) {
        StringBuilder stack_trace = new StringBuilder();
        while (e != null) {
            String error_message = e.getMessage();
            error_message = error_message == null ? "\r\n" : error_message.concat("\r\n");
            stack_trace.append(error_message);
            stack_trace.append("<br>");
            for (StackTraceElement string : e.getStackTrace()) {
                stack_trace.append(string.toString());
                stack_trace.append("<br>");
            }
            e = e.getCause();
        }
        return stack_trace.toString();
    }

    public static String getLocalHostName(){
        try{
            String HostName = InetAddress.getLocalHost().getHostName();
//            HostName.substring(0 , HostName.indexOf("."));
            return HostName;
        }catch(UnknownHostException e){
            LOGGER.error("取得hostname失败",e);
            return "";
        }

    }

    public static String getLocalIP(){
        String ip="";
//        try{
//
//            ip = InetAddress.getLocalHost().getHostAddress();
//
//
//            return ip;
//        }catch(UnknownHostException e1){
//            LOGGER.error("通过InetAddress.getLocalHost().getHostAddress()取得hostip失败",e1);
//        }

        try{
            Enumeration e = NetworkInterface.getNetworkInterfaces();
            while(e.hasMoreElements())
            {
                NetworkInterface n = (NetworkInterface) e.nextElement();
                Enumeration ee = n.getInetAddresses();
                while (ee.hasMoreElements())
                {
                    InetAddress i = (InetAddress) ee.nextElement();

                    // aka site localaddress
                    if(i.isSiteLocalAddress()){
                        return i.getHostAddress();
                    }
                }

            }
            return ip;
        }catch(SocketException e2){
            LOGGER.error("取得hostip失败",e2);
            return ip;
        }


    }

}
