package com.test.smartband.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.test.smartband.R;

/**
 * 今日睡眠情况界面
 */
public class TodaySleepActivity extends AppCompatActivity {

    private ImageView backImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_sleep);

        initView();
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 初始化
     */
    private void initView() {
        backImage = (ImageView) findViewById(R.id.iv_back);
    }
}
