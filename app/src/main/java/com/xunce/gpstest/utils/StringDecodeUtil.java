package com.xunce.gpstest.utils;

/**
 * Created by yangxu on 2017/8/7.
 */

public class StringDecodeUtil {
    public static String getIMEIFromMango(String string){
        String IMEI = "";
        String[] strArray = string.split(" ");
        for (String str: strArray) {
            if(str.contains("IMEI")){
                IMEI = str.split(":")[1];
                if(IMEI.length()==15)
                    return IMEI;
                else
                    return null;
            }
        }
        return IMEI;
    }

    public static String getIMEIFromSim808(String string){
        String IMEI = "";
        String[] strArray = string.split(";");
        for (String str: strArray) {
            if(str.contains("IMEI")){
                IMEI = str.split(":")[1];
                if(IMEI.length()==15)
                    return IMEI;
                else
                    return null;
            }
        }
        return IMEI;
    }

}
