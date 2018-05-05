package com.test.smartband.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.test.smartband.R;

import static com.test.smartband.activity.SearchForDeviceActivity.BAND;
import static com.test.smartband.activity.SearchForDeviceActivity.CONTROLLER;
import static com.test.smartband.activity.SearchForDeviceActivity.SEARCHSELECT;

/**
 * 搜索设备时蓝牙未开启界面
 */
public class BlueToothOffActivity extends AppCompatActivity {

    private static final int ENABLE_BT = 1;
    private ImageView backImageView;
    private RelativeLayout bluetoothRelativeLayout;
    private TextView disBindingTextView;
    private String intentStringExtra;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue_tooth_off);

        mContext = this;
        initView();
        Intent intent = getIntent();
        intentStringExtra = intent.getStringExtra(SEARCHSELECT);

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        bluetoothRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, ENABLE_BT);
            }
        });
        disBindingTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BlueToothOffActivity.this, MainBleActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ENABLE_BT && resultCode == Activity.RESULT_OK) {
            Intent intent = new Intent(mContext, SearchForDeviceActivity.class);
            if (intentStringExtra.equals(BAND)) {
                intent.putExtra(SEARCHSELECT, BAND);
            } else {
                intent.putExtra(SEARCHSELECT, CONTROLLER);
            }
            startActivity(intent);
            finish();//注意跳转后要销毁该activity
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initView() {
        backImageView = (ImageView) findViewById(R.id.iv_back);
        bluetoothRelativeLayout = (RelativeLayout) findViewById(R.id.re_blue_tooth);
        disBindingTextView = (TextView) findViewById(R.id.tv_disbinding);
    }
}
