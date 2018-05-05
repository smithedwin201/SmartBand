package com.test.smartband.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import com.test.smartband.R;
import com.test.smartband.other.BandNotification;
import com.test.smartband.tools.CacheUtils;

/**
 * 计步器设置界面
 */
public class PedometerActivity extends AppCompatActivity {

    private ImageView back;
    private Switch pedometerSwitch;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedometer);
        mContext = this;
        back = (ImageView) findViewById(R.id.iv_back);
        pedometerSwitch = (Switch) findViewById(R.id.switch_pedometer);
        //查询是否设置开启通知栏
        if (CacheUtils.getBoolean(mContext, CacheUtils.IS_SHOWNOTIFICATION, true)) {
            pedometerSwitch.setChecked(true);
        } else {
            pedometerSwitch.setChecked(false);
        }
        //返回
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //设置通知栏是否显示
        pedometerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                //设置是否开启通知栏，选择开启则发送消息通知MainBleActivity显示通知栏
                if (pedometerSwitch.isChecked()) {
                    CacheUtils.putBoolean(mContext, CacheUtils.IS_SHOWNOTIFICATION, true);
                    BandNotification.getInstance(PedometerActivity.this).showNotification();
                } else {
                    CacheUtils.putBoolean(mContext, CacheUtils.IS_SHOWNOTIFICATION, false);
                    manager.cancel(0);
                }
            }
        });
    }

}
