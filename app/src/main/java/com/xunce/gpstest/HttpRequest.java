package com.xunce.gpstest;

import android.util.Log;

import com.xunce.gpstest.utils.StreamToStringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by yangxu on 2017/8/7.
 */

public class HttpRequest {


    public static void postHttpCmdResult(final String url, final String body,final HttpCallback callback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] bytes = body.getBytes();
                HttpURLConnection connection;
                try{
                    URL postURL = new URL(url);
                    connection = (HttpURLConnection) postURL.openConnection();
                    connection.setConnectTimeout(5000);
                    connection.setRequestMethod("POST");
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setUseCaches(false);
                    connection.setRequestProperty("Content-Type","application/json");
                    connection.setRequestProperty("Content-Length",String.valueOf(bytes.length));
                    OutputStream outputStream = connection.getOutputStream();
                    outputStream.write(bytes);
                    outputStream.flush();
                    outputStream.close();
                    int response = connection.getResponseCode();
                    if (response == HttpURLConnection.HTTP_OK){
                        String result = StreamToStringUtil.StreamToString(connection.getInputStream());
                        callback.httpCallBack(result);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void httpGetWithUrl(final String urlString, String data, final HttpCallback callback){
        new Thread(new Runnable(){
            @Override
            public void run() {
                HttpURLConnection connection;
                try {
                    URL getURL = new URL(urlString);
                    connection = (HttpURLConnection) getURL.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    String result = StreamToStringUtil.StreamToString(connection.getInputStream());
                    Log.e("http", result);
                    callback.httpCallBack(result);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public static String getStringWithCmd(int cmd,String IMEI){
        try {
            JSONObject jsonObject = new JSONObject();
            JSONObject jsonCmd = new JSONObject();
            jsonCmd.put("c", cmd);
            jsonObject.put("imei", IMEI);
            jsonObject.put("cmd", jsonCmd);
            return jsonObject.toString();
        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    public static String getStringWithCmdAndParam(int cmd,JSONObject param,String IMEI){
        try {
            JSONObject jsonObject = new JSONObject();
            JSONObject jsonCmd = new JSONObject();
            jsonCmd.put("c", cmd);
            jsonCmd.put("param",param);
            jsonObject.put("imei", IMEI);
            jsonObject.put("cmd", jsonCmd);
            return jsonObject.toString();
        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }

}
