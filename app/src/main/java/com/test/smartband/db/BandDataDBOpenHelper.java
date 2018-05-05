package com.test.smartband.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BandDataDBOpenHelper extends SQLiteOpenHelper {

    public static final String CREATE_BAND = "create table BandData(" +
            "id integer primary key autoincrement," +
            "bodytemp REAL," +
            "battery integer," +
            "step integer," +
            "humidity REAL," +
            "roomtemp REAL," +
            "airtemp integer," +
            "pmv text," +
            "calorie REAL)";

    private Context mContext;

    public BandDataDBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BAND);
        Log.e("BandDataDBOpenHelper","创建数据库成功");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
