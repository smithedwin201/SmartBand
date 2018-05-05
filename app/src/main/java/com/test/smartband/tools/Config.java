package com.test.smartband.tools;

import android.content.Context;
import android.content.SharedPreferences.Editor;

public class Config {
    private static String user ="123";
    private static String password ="123";

    public static void setUser(String user) {
        Config.user = user;
    }

    public static void setPassword(String password) {
        Config.password = password;
    }

    public static String getUser() {
        return user;
    }

    public static String getPassword() {
        return password;
    }
    public static final String BLUETOOTH_OFF_BROADCAST = "com.test.smartband.action.BLUETOOTH_OFF_BROADCAST";
    public static final String SERVER_URL = "http://192.168.199.245/Atest/Login";
    public static final String SERVER_URLSetPhoto = "http://192.168.199.245/Atest/SetAvatar";
    public static final String SERVER_URLGetPhoto = "http://192.168.199.245/Atest/GetAvatar";


    public static final String SERVER_URL1 = "http://120.78.190.38:8080/Atest/login";
    public static final String SERVER_URLPhoto1 = "http://120.78.190.38:8080/Atest/photo";
    public static final String SERVER_URL5 = "http://39.108.146.255:8080/APPtest/servlet/login";
    public static final String SERVER_URL4 = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1498035697&di=c7e3dfc2607fc4ad07bd3e8c4b88aacd&imgtype=jpg&er=1&src=http%3A%2F%2Fpic.weifengke.com%2Fattachments%2F4%2F4494%2F016d0268f074d98191539ece686f8cfc.jpg";
    //public static final String SERVER_URL = null;

//   public static final String SERVER_URL = "http://192.168.199.119:8080/TestSecretServer/servlet/login";

    public static final String APP_ID = "com.smartband.dayunwu";

    public static final String KEY_TOKEN = "token";
    public static final String KEY_ACTION = "action";
    public static final String KEY_PHONE_NUM = "phone";
//    public static final String KEY_PHONE_MD5 = "phone_md5";
    public static final String KEY_PHONE_MD5 = "phone_number";

    public static final String KEY_STATUS = "login_rp";
//    public static final String KEY_STATUS = "isLogin";

//    public static final String KEY_CODE = "code";
    public static final String KEY_CODE = "password";
    public static final String KEY_PWD = "password";
    public static final String KEY_CONTACTS = "contacts";
    public static final String KEY_PAGE = "page";
    public static final String KEY_PERPAGE = "perpage";
    public static final String KEY_MSG_ID = "msgId";
    public static final String KEY_MSG = "msg";
    public static final String KEY_COMMENT = "comments";
    public static final String KEY_TIMELINE = "timeline";
    public static final String KEY_CONTENT = "content";


    public static final int RESULT_STATUS_SUCCESS = 1;
    public static final int RESULT_STATUS_FAIL = 0;
    public static final int RESULT_STATUS_INVALID_TOKEN = 2;


    public static final String CHARSET = "utf-8";

    public static final String ACTION_GET_CODE = "send_pass";
    public static final String ACTION_LOGIN = "login";
    public static final String ACTION_GETAvatar = "get_avatar";
    public static final String ACTION_UPLOAD_CONTACTS = "upload_contacts";
    public static final String ACTION_TIMELINE = "timeline";
    public static final String ACTION_GET_COMMENT = "get_comment";
    public static final String ACTION_PUB_COMMENT = "pub_comment";
    public static final String ACTION_PUBLISH = "publish";

    public static final int ACTIVITY_RESULT_NEED_REFRESH = 10000;

    /**
     * 获取本地的token
     * @param context
     * @return
     */

    public static String getCachedToken(Context context) {
        return context.getSharedPreferences(APP_ID, Context.MODE_PRIVATE)
                .getString(KEY_TOKEN, null);
    }
    /**
     * 将token缓存到本地
     */
    public static void cacheToken(Context context, String token) {
        Editor editor = context.getSharedPreferences(APP_ID, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_TOKEN, token);
        editor.commit();
    }

    public static String getCachedPhoneNum(Context context) {
        return context.getSharedPreferences(APP_ID, Context.MODE_PRIVATE)
                .getString(KEY_PHONE_NUM, null);
    }

    public static void cachePhoneNum(Context context, String phoneNum) {
        Editor editor = context.getSharedPreferences(APP_ID, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_PHONE_NUM, phoneNum);
        editor.commit();
    }

}
