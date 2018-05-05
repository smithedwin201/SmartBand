package com.test.smartband.net;

import android.util.Log;

import com.test.smartband.tools.Config;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * 网络访问工具类
 */
public class HttpUtil {

    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

    /**
     * 异步GET请求 (直接请求，不用post一个表单)
     * 下载图片和请求Json数据
     */
    private static void sendOkHttpRequest(String address, okhttp3.Callback callback) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //设置超时时间 (还可以设置缓存大小，此处未设置)
        builder.connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS);
        OkHttpClient client = builder.build();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * 异步POST请求 (请求时需要post一个表单)
     */
    private static void sendOkHttpRequest(String address, FormBody body, okhttp3.Callback callback) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //设置超时时间 (还可以设置缓存大小，此处未设置)
        builder.connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS);
        OkHttpClient client = builder.build();
        Request request = new Request.Builder().url(address).post(body).build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * 用户登录请求
     * <p>
     * 封装成的请求数据体为：action=login&loginJson={"phone_number":"123","password":"123"}
     * action:login
     * loginJson:{
     * <p>
     * }
     * action=login&loginJson={
     * phone_number:xxxxx,
     * password:xxx
     * }
     */
    public static void login(String phone_md5, String pwd, okhttp3.Callback callback) {
        FormBody body = null;
        try {
            JSONObject object = new JSONObject();
            object.put(Config.KEY_PHONE_MD5, phone_md5);
            object.put(Config.KEY_PWD, pwd);
            body = new FormBody.Builder()
                    .add(Config.KEY_ACTION, Config.ACTION_LOGIN)
                    .add("checkJson", object.toString())
                    .build();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendOkHttpRequest(Config.SERVER_URL, body, callback);
    }

//    /**
//     * 上传用户的头像   //创峰代码
//     */
//    public static void uploadAvatar(String path, okhttp3.Callback callback) {
//        OkHttpClient.Builder builder = new OkHttpClient.Builder();
//        //设置超时时间 (还可以设置缓存大小，此处未设置)
//        builder.connectTimeout(15, TimeUnit.SECONDS)
//                .writeTimeout(20, TimeUnit.SECONDS)
//                .readTimeout(20, TimeUnit.SECONDS);
//        OkHttpClient client = builder.build();
//        File file = new File(path);
//        FormBody body = null;
//        body = new FormBody.Builder()
//                .add(Config.KEY_ACTION, "photoJson")
//                .build();
//        RequestBody requestBody = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart(Config.KEY_ACTION, "photo")
//                .addFormDataPart("image", "wangshu.jpg", RequestBody.create(MEDIA_TYPE_PNG, file)
//                .build();
//        Request request = new Request.Builder()
//                .url(Config.SERVER_URL)
//                .patch(body)
//                .post(RequestBody.create(MEDIA_TYPE_PNG, file))
////                .post(RequestBody.create(MEDIA_TYPE_PNG, file))
//                .build();
//        Log.e("HttpUtil:", request.toString());
//        client.newCall(request).enqueue(callback);
//    }

    public static void downloadAvatar(String phone_md5, String pwd, okhttp3.Callback callback) {
        FormBody body = null;
        try {
            JSONObject object = new JSONObject();
            object.put(Config.KEY_PHONE_MD5, phone_md5);
            object.put(Config.KEY_PWD, pwd);
            body = new FormBody.Builder()
                    .add(Config.KEY_ACTION, Config.ACTION_GETAvatar)
                    .add("checkJson", object.toString())
                    .build();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendOkHttpRequest(Config.SERVER_URLGetPhoto, body, callback);
    }


//        /**
//     * 上传用户的头像,需要对应用户    //新代码
//     */
//    public static void uploadAvatar(final String temp_path) {
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                HttpURLConnection conn = null;
//                String BOUNDARY = "|||"; // request头和上传文件内容分隔符
//                String rsp = null ;
//                //  Toast.makeText(this,"woyunxinle",Toast.LENGTH_SHORT).show();
//                try {
//                    //URL url = new URL("http://192.168.199.120:8080/APPtest/servlet/login");
//                    //URL url = new URL("http://192.168.43.107/Hello/HelloServlet");
//                    URL url = new URL(Config.SERVER_URLphoto);
//                    conn = (HttpURLConnection) url.openConnection();
//                    conn.setConnectTimeout(5000);
//                    conn.setReadTimeout(30000);//缓存的最长时间
//                    conn.setDoOutput(true);//允许输出
//                    conn.setDoInput(true);//允许输入
//                    conn.setUseCaches(false);
//                    conn.setRequestMethod("POST");
//                    //setRequestProperty主要是设置HttpURLConnection请求头里面的属性
//                    //比如Cookie、User-Agent（浏览器类型）等等，具体可以看HTTP头相关的材料
//                    //至于要设置什么这个要看服务器端的约定
//                    conn.setRequestProperty("Connection", "Keep-Alive");
//                    conn.setRequestProperty("User-Agent",
//                            "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
//                    conn.setRequestProperty("Content-Type",
//                            "multipart/form-data; boundary=" + BOUNDARY);
//                    OutputStream out = new DataOutputStream(conn.getOutputStream());
//                    File file = new File(temp_path);
//                    String filename = file.getName();
//                    String contentType = "";
//                    if (filename.endsWith(".png")) {
//                        contentType = "image/png";
//                    }
//                    if (filename.endsWith(".jpg")) {
//                        contentType = "image/jpg";
//                    }
//                    if (filename.endsWith(".gif")) {
//                        contentType = "image/gif";
//                    }
//                    if (filename.endsWith(".bmp")) {
//                        contentType = "image/bmp";
//                    }
//                    if (contentType == null || contentType.equals("")) {
//                        contentType = "application/octet-stream";
//                    }
//                    StringBuffer strBuf = new StringBuffer();
//                    strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
//                    strBuf.append("Content-Disposition: form-data; name=\"" + "file1"
//                            + "\"; filename=\"" + filename + "\"\r\n");
//                    strBuf.append("Content-Type:" + contentType + "\r\n\r\n");
//                    out.write(strBuf.toString().getBytes());
//
////                    DataInputStream in = new DataInputStream(new FileInputStream(file));
////                    int bytes = 0;
////                    byte[] bufferOut = new byte[1024];
////                    while ((bytes = in.read(bufferOut)) != -1) {
////                        out.write(bufferOut, 0, bytes);
////                    }
////                    in.close();
//                    byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
//                    out.write(endData);
//                    out.flush();
//                    out.close();
//
//                    // 读取返回数据
//                  conn.getInputStream();
////                    StringBuffer buffer = new StringBuffer();
//
////                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
////                    String line = null;
////                    while ((line = reader.readLine()) != null) {
////                        buffer.append(line).append("\n");
////                    }
////                    rsp = buffer.toString();
////                    reader.close();
////                    reader = null;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    if (conn != null) {
//                        conn.disconnect();
//                        conn = null;
//                    }
//                }
//                Log.e("woshishei", "upLoadToServlet: "+rsp );
////        System.out.println("文件url为：" + rsp);
//            }
//        }).start();
//
//    }
    private static final String TAG = "uploadFile";
//简洁板代码

    public static void uploadAvatar(final String temp_path) {
        new Thread(){
            @Override
            public void run()
            {
                String end = "\r\n";

                String Hyphens = "--";

                String boundary = "*****";

                try

                {

                    URL url = new URL(Config.SERVER_URLSetPhoto);

                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

      /* 允许Input、Output，不使用Cache */

                    con.setDoInput(true);

                    con.setDoOutput(true);

                    con.setUseCaches(false);

      /* 设定传送的method=POST */

                    con.setRequestMethod("POST");

      /* setRequestProperty */

                    con.setRequestProperty("Connection", "Keep-Alive");

                    con.setRequestProperty("Charset", "UTF-8");

                    con.setRequestProperty("Content-Type",

                            "multipart/form-data;boundary=" + boundary);

      /* 设定DataOutputStream */
                    File file = new File(temp_path);
                    String filename = file.getName();

                    DataOutputStream ds = new DataOutputStream(con.getOutputStream());

                    ds.writeBytes(Hyphens + boundary + end);

                    ds.writeBytes( "Content-Disposition: form-data; name=\""+Config.getUser()+"\"; filename=\""
                            + file.getName() + "\"" + end);

                    ds.writeBytes(end);

      /* 取得文件的FileInputStream */

                    FileInputStream fStream = new FileInputStream(temp_path);
                    Log.e(TAG, "ssss"+filename);
      /* 设定每次写入1024bytes */

                    int bufferSize = 1024;

                    byte[] buffer = new byte[bufferSize];

                    int length = -1;

      /* 从文件读取数据到缓冲区 */

                    while ((length = fStream.read(buffer)) != -1)

                    {

        /* 将数据写入DataOutputStream中 */

                        ds.write(buffer, 0, length);

                    }

                    ds.writeBytes(end);

                    ds.writeBytes(Hyphens + boundary + Hyphens + end);

                    fStream.close();

                    ds.flush();

      /* 取得Response内容 */

                    InputStream is = con.getInputStream();

                    int ch;

                    StringBuffer b = new StringBuffer();

                    while ((ch = is.read()) != -1)

                    {

                        b.append((char) ch);

                    }
con.getInputStream();

                    Log.e(TAG, "uploadAvatar: 上传成功");


                    ds.close();

                } catch (Exception e)

                {

                    Log.e(TAG, "uploadAvatar: 上传失败");
                    Log.e(TAG,Log.getStackTraceString(e));




                }
            }
        }.start();


    }

}

