package com.test.smartband.other;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;

import com.test.smartband.R;
import com.test.smartband.activity.MainBleActivity;
import com.test.smartband.tools.CacheUtils;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * 显示在通知栏的通知
 */

public class BandNotification {

    private Context mContext;
    private NotificationManager mNotificationManager = null;
    private android.app.Notification.Builder notificationBuilder;
    private static BandNotification mNotification = null;
    private boolean isConnect = false;
    private int mStep = 0;

    private BandNotification(Context context) {
        mContext = context;
        mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationBuilder = new android.app.Notification.Builder(context);
    }

    public static BandNotification getInstance(Context context) {
        if (mNotification == null) {
            mNotification = new BandNotification(context);
        }
        return mNotification;
    }

    public void showNotification(boolean connect, int step) {
        isConnect = connect;
        mStep = step;
        showNotification();
    }

    public void showNotification() {
        if (CacheUtils.getBoolean(mContext, CacheUtils.IS_SHOWNOTIFICATION)) {
            notificationBuilder.setSmallIcon(R.drawable.trademark);
            notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.applogo));
            if (isConnect) {
                String contentTitle = "今日步数：" + mStep + "步";
                String contentText = "运动，更好的每一天";
                notificationBuilder.setContentTitle(contentTitle);
                notificationBuilder.setContentText(contentText);
            }else {
                notificationBuilder.setContentTitle("空调手环");
                notificationBuilder.setContentText("手环未连接");
            }
            //点击通知栏就回到该APP中
            Intent notificationIntent = new Intent(mContext, MainBleActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0);
            notificationBuilder.setContentIntent(contentIntent);
            Notification notification = notificationBuilder.build();
            //标志一直显示在通知栏
            notification.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP | Notification.FLAG_ONGOING_EVENT;
            mNotificationManager.notify(0,notification);
        }
    }
}
