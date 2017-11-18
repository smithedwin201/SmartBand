package com.test.smartband.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.test.smartband.R;

import static com.test.smartband.activity.SearchForDeviceActivity.BAND;
import static com.test.smartband.activity.SearchForDeviceActivity.CONTROLLER;
import static com.test.smartband.activity.SearchForDeviceActivity.SEARCHSELECT;

/**
 * 绑定新设备
 */
public class NewDeviceActivity extends AppCompatActivity {

    private RelativeLayout bandRelativeLayout;
    private RelativeLayout controllerRelativeLayout;
    private RelativeLayout airConditionRelativeLayout;
    private ImageView cancelImageView;
    private Context mContext;
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_device);
        mContext = this;
        initView();
        addListener();
    }

    private void addListener() {

        NewDeviceListener listener = new NewDeviceListener();
        bandRelativeLayout.setOnClickListener(listener);
        controllerRelativeLayout.setOnClickListener(listener);
        airConditionRelativeLayout.setOnClickListener(listener);
        cancelImageView.setOnClickListener(listener);
    }

    private void initView() {
        cancelImageView = (ImageView) findViewById(R.id.iv_cancel);
        bandRelativeLayout = (RelativeLayout) findViewById(R.id.re_band);
        controllerRelativeLayout = (RelativeLayout) findViewById(R.id.re_air_condition_controller);
        airConditionRelativeLayout = (RelativeLayout) findViewById(R.id.re_air_condition);

        BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    private class NewDeviceListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.re_band:
                    if (!mBluetoothAdapter.isEnabled()) {
                        Intent bandIntent = new Intent(mContext, BlueToothOffActivity.class);
                        bandIntent.putExtra(SEARCHSELECT, BAND);//BAND，即要绑定的是手环
                        startActivity(bandIntent);
                    } else {
                        Intent bandIntent = new Intent(mContext, SearchForDeviceActivity.class);
                        bandIntent.putExtra(SEARCHSELECT, BAND);
                        startActivity(bandIntent);
                    }
                    break;
                case R.id.re_air_condition_controller:
                    if (!mBluetoothAdapter.isEnabled()) {
                        Intent intent = new Intent(mContext, BlueToothOffActivity.class);
                        intent.putExtra(SEARCHSELECT, CONTROLLER);//设置标志CONTROLLER，即要绑定的是终端
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(mContext, SearchForDeviceActivity.class);
                        intent.putExtra(SEARCHSELECT, CONTROLLER);
                        startActivity(intent);
                    }
                    break;
                case R.id.re_air_condition:
                    startActivity(new Intent(mContext, MatchAirConditionActivity.class));
                    break;
                case R.id.iv_cancel:
                    finish();
                    break;
                default:
                    break;
            }
        }
    }

}
