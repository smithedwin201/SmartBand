package com.test.smartband.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.test.smartband.R;

public class TodaySportActivity extends AppCompatActivity {

    private ImageView backImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_step);

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
