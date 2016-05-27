package com.James.demo.sample;

import java.util.concurrent.TimeUnit;

/**
 * Created by James on 16/5/27.
 */
public class Launch {

    public String buildString(int length) throws Exception{
        String result = "";
        for (int i = 0; i < length; i++) {
            result += (char)(i%26 + 'a');
            TimeUnit.SECONDS.sleep(1);
            System.out.println(result);
        }
        return result;
    }

    public static void main(String[] args) {
        try{
            new Launch().buildString(2);
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
