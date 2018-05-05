package com.test.smartband.net;

import android.os.AsyncTask;
import android.util.Log;

import com.test.smartband.tools.Config;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;


public class Netconnect {

    public Netconnect(final String url, final HttpMethod method, final SuccessCallback successCallback,
                      final FailCallback failCallback, final String... kvs) {

        /**
         * Netconnect的参数为什么是final，因为内部类无法访问外部的非final变量
         */

        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                //对参数对进行遍历
                StringBuffer paramsStr = new StringBuffer();
                paramsStr.append(kvs[0]).append("=").append(kvs[1]).append("&");
                paramsStr.append("loginJson").append("=");
                try {
                    JSONObject object = new JSONObject();
                    for (int i = 2; i < kvs.length; i += 2) {
                        object.put(kvs[i], kvs[i+1]);
                    }
                    paramsStr.append(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    URLConnection connection;
                    switch (method) {
                        case POST:
                            //向服务器发出请求
                            connection = new URL(url).openConnection();
                            connection.setDoOutput(true);
                            connection.setDoInput(true);
                            BufferedWriter writer = new BufferedWriter(
                                    new OutputStreamWriter(connection.getOutputStream(), Config.CHARSET));//Config.CHARSET是编码方式
                            //向服务器上传参数
                            writer.write(paramsStr.toString());
                            //立即发送到服务端
                            writer.flush();
                            break;
                        default:
                            //get方式是直接写在URL里面向服务器上传参数
                            connection = new URL(url + "?" + paramsStr.toString()).openConnection();
                            break;
                    }

                    System.out.println("Request url:" + connection.getURL());
                    System.out.println("Request data:" + paramsStr);
                    Log.e("连接","Request url:" + connection.getURL());

                    //读取返回的结果，将返回的结果输出
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream(), Config.CHARSET));
                    String line = null;
                    StringBuffer result = new StringBuffer();
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                }
                    System.out.println("Result:" + result);
                    return result.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //如果返回null，说明通信失败。
                return null;
            }

            @Override
            protected void onPostExecute(String result) {

                if (result != null) {
                    if (successCallback != null) {
                        successCallback.onSuccess(result);
                    } else {
                        if (failCallback != null) {
                            failCallback.onFail();
                        }
                    }
                }

                super.onPostExecute(result);

            }
        }.execute();

    }

    public static interface SuccessCallback {
        void onSuccess(String result);
    }

    public static interface FailCallback {
        void onFail();
    }


}
