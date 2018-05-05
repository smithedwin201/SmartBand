package com.test.smartband.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.test.smartband.db.BandDataDBOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class BandDataDB {
    /**
     * 数据库名
     */
    public static final String DB_NAME = "BandData.db";

    /**
     * 数据库版本
     */
    public static final int DATABASE_VERSION = 1;

    private static BandDataDB bandDataDB;
    private SQLiteDatabase db;

    /**
     * 将构造函数私有化
     */
    private BandDataDB(Context context) {
        BandDataDBOpenHelper dbOpenHelper = new BandDataDBOpenHelper(context,
                DB_NAME, null, DATABASE_VERSION);
        db = dbOpenHelper.getWritableDatabase();
    }

    /**
     * 获取BandDataDB的实例
     */
    public synchronized static BandDataDB gerInstance(Context context) {
        if (bandDataDB == null) {
            bandDataDB = new BandDataDB(context);
        }
        return bandDataDB;
    }

    /**
     * 将BandData实例存储到数据库中
     */
    public void saveBandData(BandData bandData) {
        if (bandData != null) {
            ContentValues values = new ContentValues();
            values.put("bodytemp",bandData.getBodyTemp());
            values.put("battery",bandData.getBattery());
            values.put("step",bandData.getStep());
            values.put("humidity",bandData.getHumidity());
            values.put("roomtemp",bandData.getRoomTemp());
            values.put("airtemp",bandData.getAirconditionTemp());
            values.put("pmv",bandData.getPmvLevel());
            values.put("calorie",bandData.getCalorie());
            db.insert("BandData", null, values);
        }
    }

    /**
     * 读取所有BandData的信息
     */
    public List<BandData> loadBandData() {
        List<BandData> list = new ArrayList<BandData>();
        Cursor cursor = db.query("BandData", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                BandData bandData = new BandData();
                bandData.setId(cursor.getInt(cursor.getColumnIndex("id")));
                bandData.setBodyTemp(cursor.getFloat(cursor.getColumnIndex("bodytemp")));
                bandData.setBattery(cursor.getInt(cursor.getColumnIndex("battery")));
                bandData.setStep(cursor.getInt(cursor.getColumnIndex("step")));
                bandData.setHumidity(cursor.getFloat(cursor.getColumnIndex("humidity")));
                bandData.setRoomTemp(cursor.getFloat(cursor.getColumnIndex("roomtemp")));
                bandData.setAirconditionTemp(cursor.getInt(cursor.getColumnIndex("airtemp")));
                bandData.setPmvLevel(cursor.getString(cursor.getColumnIndex("pmv")));
                bandData.setCalorie(cursor.getFloat(cursor.getColumnIndex("calorie")));
                list.add(bandData);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }
}
