package com.test.smartband.fragment;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.test.smartband.R;
import com.test.smartband.activity.LocationData;
import com.test.smartband.model.BandData;
import com.test.smartband.service.BluetoothLeService;
import com.test.smartband.tools.Config;
import com.test.smartband.view.BatteryView;

/**
 * Created by chuangfeng on 2016/4/24.
 */
public class FragmentHome extends Fragment  {

    private View view;
    private TextView tv_humidity;
    private TextView tv_roomRemperature;
    private TextView tv_bodyTemperature;
    private TextView tv_runAccount;
    private TextView tv_pmv;
    private TextView tv_air_condition;

    private TextWatcher textWatcher;

    private Context context;
    private SharedPreferences pre;
    private SharedPreferences.Editor editor;
    public LocationData handleData;//处理定位数据的类
    private IntentFilter intentFilter;
    private LocalBroadcastManager localBroadcastManager;
    private BatteryView batteryview;
    private ImageView aircondition_iv;
    private LocalReceiver localReceiver;
    private TextView connect;
    private ProgressBar progressBar;
    private RelativeLayout connectStateLayout;
    private LinearLayout blueToothOffLayout;
    private TextView electricQuantity;


    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        Log.e("FragmentHome", "onAttach");

        try{

            pre = activity.getSharedPreferences("LOCATIONDATA", Context.MODE_PRIVATE);
            editor = pre.edit();

            context = getActivity().getApplicationContext();
            handleData = new LocationData(context, pre);

        }catch(ClassCastException e){
            throw new ClassCastException(activity.toString()+"must implement OnArticleSelectedListener");
        }
    }

    public void onResume() {
        super.onResume();
        Log.e("FragmentHome", "onResume");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        initView();
        registerLocalReceiver();
        Log.e("FragmentHome", "onCreateView");

        return view;
    }

    /**
     * 2016.8.27
     * 注册本地广播
     */
    private void registerLocalReceiver() {
        intentFilter = new IntentFilter();
        intentFilter.addAction(Config.BLUETOOTH_OFF_BROADCAST);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SCANNING);
        intentFilter.addAction(BluetoothLeService.ACTION_BANDDATA);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        localReceiver = new LocalReceiver();
        getActivity().registerReceiver(localReceiver, intentFilter);
    }

    private class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//            Log.e("FragmentHome", "收到广播");
            final String action = intent.getAction();
            if (Config.BLUETOOTH_OFF_BROADCAST.equals(action)) {
                blueToothOffLayout.setVisibility(View.VISIBLE);
                connectStateLayout.setVisibility(View.GONE);
            }
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {//蓝牙状态监听
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF: //手机蓝牙关闭
                        blueToothOffLayout.setVisibility(View.VISIBLE);
                        connectStateLayout.setVisibility(View.GONE);
                        updateData(null);
                        break;
                    case BluetoothAdapter.STATE_ON: //手机蓝牙开启
                        blueToothOffLayout.setVisibility(View.GONE);
                        connectStateLayout.setVisibility(View.VISIBLE);
                        break;
                }
            }

            if (BluetoothLeService.ACTION_GATT_SCANNING.equals(action)) {
                connect.setText("正在搜索");
                progressBar.setVisibility(View.VISIBLE);
            } else if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                connect.setText("正在连接");
                progressBar.setVisibility(View.VISIBLE);
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                if (BluetoothLeService.ISRECONNECT) {
                    connect.setText("连接断开,重新搜索");
                }else {
                    connect.setText("连接断开");
                    updateData(null);
                }
                progressBar.setVisibility(View.VISIBLE);
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                connect.setText("已连接");
                progressBar.setVisibility(View.GONE);
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {

            } else if (BluetoothLeService.ACTION_BANDDATA.equals(action)) {
                //获取发送到手机的数据
                updateData((BandData) intent.getSerializableExtra("band_data"));
            }
        }
    }

    /**
     * 2016.8.27
     * 更新数据
     */
    private void updateData(BandData bandData) {
        if (bandData != null) {
            tv_bodyTemperature.setText((bandData.getBodyTemp()) + "");
            tv_roomRemperature.setText(bandData.getRoomTemp() + "");
            tv_humidity.setText(bandData.getHumidity() + "");
            tv_runAccount.setText(bandData.getStep() + "");
            tv_air_condition.setText(bandData.getAirconditionTemp() + "");
            tv_pmv.setText(bandData.getPmvLevel() + "");
            //显示电量
            int battery = bandData.getBattery();
            if (battery != 0) {
                if (bandData.isCharge()) {
                    connect.setText("已连接（充电中）");
                }else {
                    connect.setText("已连接");
                }

                batteryview.setPower(bandData.isCharge(), battery);
                electricQuantity.setText(bandData.getBattery() + "");
                batteryview.setVisibility(View.VISIBLE);
                electricQuantity.setVisibility(View.VISIBLE);
            } else {
                batteryview.setVisibility(View.GONE);
                electricQuantity.setVisibility(View.GONE);
            }
            //显示连接空调的标志
            if (bandData.getAirconditionTemp() != 0) {
                aircondition_iv.setVisibility(View.VISIBLE);
            } else {
                aircondition_iv.setVisibility(View.GONE);
            }
        } else {
            tv_bodyTemperature.setText("0.0");
            tv_roomRemperature.setText("0.0");
            tv_humidity.setText("0.0");
            tv_runAccount.setText("0");
            tv_air_condition.setText("0.0");
            tv_pmv.setText("--");
            batteryview.setVisibility(View.GONE);
            aircondition_iv.setVisibility(View.GONE);
            electricQuantity.setVisibility(View.GONE);
        }

    }

    private void initView() {
        connectStateLayout = (RelativeLayout) view.findViewById(R.id.connectStateLayout);
        blueToothOffLayout = (LinearLayout) view.findViewById(R.id.bluetooth_off_linearlayout);

        tv_bodyTemperature = (TextView) view.findViewById(R.id.body_temperature);
        tv_roomRemperature = (TextView) view.findViewById(R.id.room_temperature);
        tv_humidity = (TextView) view.findViewById(R.id.humidity);
        tv_runAccount = (TextView) view.findViewById(R.id.run);
        tv_pmv = (TextView) view.findViewById(R.id.pmv);
        tv_air_condition = (TextView) view.findViewById(R.id.air_condition);
        batteryview = (BatteryView)view.findViewById(R.id.batteryview);
        aircondition_iv = (ImageView) view.findViewById(R.id.iv_air_condition);
        progressBar = (ProgressBar) view.findViewById(R.id.connectStateProgress);
        connect = (TextView) view.findViewById(R.id.connectState);
        electricQuantity = (TextView) view.findViewById(R.id.electric_quantity);

        blueToothOffLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                startActivity(enableBtIntent);
            }
        });

//        connect.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
    }

    @Override
    public void onDestroyView() {
        Log.e("FragmentHome", "onDestroyView");
        super.onDestroyView();
        localBroadcastManager.unregisterReceiver(localReceiver);
    }
}
