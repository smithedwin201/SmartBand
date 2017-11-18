package com.test.smartband.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.test.smartband.R;

/**
 * 账号绑定活动界面
 */

public class AccountBindingActivity extends AppCompatActivity {

    private ImageView backImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binding_account);
        initView();
        Log.e("AccountBindingActivity", "onCreate");

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void onResume() {
        super.onResume();
        Log.e("AccountBindingActivity", "onResume");

    }

        private void initView() {
        backImageView = (ImageView) findViewById(R.id.iv_back);

    }
}
