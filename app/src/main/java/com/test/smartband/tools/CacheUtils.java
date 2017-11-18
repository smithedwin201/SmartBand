package com.test.smartband.tools;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 通过SharedPreferencs缓存数据 到xml文件中
 */
public class CacheUtils {
    public static final String CONFIG_SP = "config_sp";// config_sp.xml 文件  存放位置 ：/data/data/《包名》/shared_prefes
    public static final String IS_FIRST_USE = "is_first_use";// 是否第一次使用应用
    public static final String APK_UPDATE = "apk_update";//是否要更新版本
    private static SharedPreferences mSp;
    public static final String IS_SHOWNOTIFICATION = "is_show_notification";//是否显示通知栏
    public static final String AIR_CONDITION_BRAND = "air_condition_brand";//保存空调品牌
    public static final String IS_OPEN_RUN_SOUND = "is_open_run_sound"; //是否设置跑步是开启声音播放
    public static final String STEP_NUMBER_SETTING_TYPE = "step_number_setting_type";//设置步数目标是的方式，默认还是自定义
    public static final String STEP_TARGET_NUMBER = "step_target_number";//步数目标
    public static final String WEIGHT_TARGET = "weight_target";//步数目标

    public static final String IS_LOGIN = "is_logout";//用户是否已经登录

    private static SharedPreferences getPreferencs(Context context) {
        if (mSp == null) {
            mSp = context.getSharedPreferences(CONFIG_SP, Context.MODE_PRIVATE);
        }
        return mSp;
    }

    // 保存布尔数据
    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences sp = getPreferencs(context);
        sp.edit().putBoolean(key, value).commit();
    }

    // 取布尔数据 ,返回的是false 默认值
    public static boolean getBoolean(Context context, String key) {
        SharedPreferences sp = getPreferencs(context);
        return sp.getBoolean(key, false);
    }

    // 取布尔数据 ,默认返回的是false
    public static boolean getBoolean(Context context, String key, boolean defvalue) {
        SharedPreferences sp = getPreferencs(context);
        return sp.getBoolean(key, defvalue);
    }

    // 保存字符串
    public static void putString(Context context, String key, String value) {
        SharedPreferences sp = getPreferencs(context);
        sp.edit().putString(key, value).commit();
    }

    // 取字符串数据 ,默认返回的是 null
    public static String getString(Context context, String key) {
        SharedPreferences sp = getPreferencs(context);
        return sp.getString(key, null);
    }

    // 取字符串数据 ,返回的是传递过来的值
    public static String getString(Context context, String key, String defvalue) {
        SharedPreferences sp = getPreferencs(context);
        return sp.getString(key, defvalue);
    }
    //保存整数
    public static void putInt(Context context, String key, int value) {
        SharedPreferences sp = getPreferencs(context);
        sp.edit().putInt(key, value).commit();
    }
    //获取整数
    public static int getInt(Context context, String key) {
        SharedPreferences sp = getPreferencs(context);
         return sp.getInt(key, 0);
    }
    //获取整数
    public static int getInt(Context context, String key, int value) {
        SharedPreferences sp = getPreferencs(context);
        return sp.getInt(key, value);
    }


}
