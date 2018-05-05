package com.test.smartband.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.test.smartband.R;

import static com.test.smartband.activity.SearchForDeviceActivity.BAND;
import static com.test.smartband.activity.SearchForDeviceActivity.CONTROLLER;
import static com.test.smartband.activity.SearchForDeviceActivity.SEARCHSELECT;

public class SearchDeviceFailureActivity extends AppCompatActivity {

    private Context mContext;
    private TextView searchFailureTextView;
    private TextView searchTipsTextView;
    private Button searchAgainButton;
    private TextView searchExitTextView;
    private String intentStringExtra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_device_failure);
        mContext = this;

        initView();
        Intent intent = getIntent();
        intentStringExtra = intent.getStringExtra(SEARCHSELECT);
        if (intentStringExtra.equals(BAND)) {
            searchFailureTextView.setText("没有找到您的手环");
            searchTipsTextView.setText("请将要绑定的手环放置在您的手机附近");
            searchAgainButton.setText("再次搜索手环");
        } else if (intentStringExtra.equals(CONTROLLER)) {
            searchFailureTextView.setText("没有找到您的终端");
            searchTipsTextView.setText("请将您的手机靠近将要绑定的终端");
            searchAgainButton.setText("再次搜索终端");
        }

        searchAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SearchForDeviceActivity.class);
                intent.putExtra(SEARCHSELECT, intentStringExtra);
                startActivity(intent);
                finish();
            }
        });

        searchExitTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, MainBleActivity.class));
                finish();
            }
        });
    }

    private void initView() {
        searchFailureTextView = (TextView) findViewById(R.id.tv_search_failure);
        searchTipsTextView = (TextView) findViewById(R.id.tv_search_tips);
        searchAgainButton = (Button) findViewById(R.id.btn_search_again);
        searchExitTextView = (TextView) findViewById(R.id.tv_search_exit);
    }

    @Override
    public void onBackPressed() {
    }
}
