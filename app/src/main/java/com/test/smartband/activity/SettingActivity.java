package com.test.smartband.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.test.smartband.R;
import com.test.smartband.tools.CacheUtils;

public class SettingActivity extends AppCompatActivity {

    private ImageView backImageView;
    private Button btn_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
        Log.e("SettingActivity", "onCreate");

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CacheUtils.putBoolean(SettingActivity.this, CacheUtils.IS_LOGIN, false);
                Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                //根据Activity的声明周期退出程序
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //注意本行的FLAG设置
                startActivity(intent);
                finish();
                //System.exit(0);
            }
        });
    }

    public void onResume() {
        super.onResume();
        Log.e("SettingActivity", "onResume");

    }

    private void initView() {
        backImageView = (ImageView) findViewById(R.id.iv_back);
        btn_logout = (Button) this.findViewById(R.id.logout_button);
    }

    @Override
    public void onDestroy() {
        Log.e("SettingActivity", "onDestroy");
        super.onDestroy();
    }
}
