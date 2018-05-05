package com.test.smartband.net;


import com.test.smartband.tools.Config;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 登录通信类
 */

public class Login {
    //向服务器发送手机的Md5码和手机验证码进行确认，如果确认成功则允许登录；
    public Login(String phone_md5, String code, final SuccessCallback successCallback, final FailCallback failCallback) {

        new Netconnect(Config.SERVER_URL, HttpMethod.POST, new Netconnect.SuccessCallback() {
            @Override
            public void onSuccess(String result) {
                if (successCallback != null) {
                    try {
                        //获取json数据，如果返回值为RESULT_STATUS_SUCCESS，说明手机确认成功，可以登录，否则失败，不可登录。
                        JSONObject object = new JSONObject(result);
                        switch (object.getInt(Config.KEY_STATUS)) {
                            case Config.RESULT_STATUS_SUCCESS:
                                if (successCallback != null) {
//                                    successCallback.onSuccess(object.getString(Config.KEY_TOKEN));
                                    successCallback.onSuccess();
                                }
                                break;
                            default:
                                if (failCallback != null) {
                                    failCallback.onFail();
                                }
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, new Netconnect.FailCallback() {
            @Override
            public void onFail() {
                if (failCallback != null) {
                    failCallback.onFail();
                }

            }
        }, Config.KEY_ACTION, Config.ACTION_LOGIN,
                Config.KEY_PHONE_MD5,phone_md5, Config.KEY_CODE, code);
    }

    public static interface SuccessCallback{
        void onSuccess();
    }

    public static interface FailCallback {
        void onFail();
    }

}
