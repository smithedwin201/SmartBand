package com.test.smartband.controlsubfrags;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.test.smartband.R;
import com.test.smartband.service.BluetoothLeService;
import com.test.smartband.model.BandData;

/**
 * Created by Administrator on 2016/5/3 0003.
 */
public class FragmentSubComfort extends Fragment {

    private static final String KEY_CONTENT = "舒适度Fragment";
    private Bundle bundle;
    private View view;
    private BandData bandData;
    private IntentFilter intentFilter;
    private LocalReceiver localReceiver;
    private LocalBroadcastManager localBroadcastManager;
    private TextView tv_pmv;
    private TextView tv_body_temper;
    private TextView tv_room_temper;
    private TextView tv_humidity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
            bundle = savedInstanceState.getBundle(KEY_CONTENT);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_comfort, container, false);
        initView();
        registerLocalReceiver();
        return view;
    }

    private void initView() {
        tv_pmv = (TextView) view.findViewById(R.id.text_pmv);
        tv_body_temper = (TextView) view.findViewById(R.id.id_comfort_body_temper);
        tv_room_temper = (TextView) view.findViewById(R.id.id_comfort_room_temper);
        tv_humidity = (TextView) view.findViewById(R.id.id_comfort_humidity);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle(KEY_CONTENT, bundle);
    }

    private void updateData(BandData bandData) {
        tv_pmv.setText(bandData.getPmvLevel());
        tv_body_temper.setText("体温：" +  bandData.getBodyTemp() + "℃");
        tv_room_temper.setText("室温：" +bandData.getRoomTemp() + "℃");
        tv_humidity.setText("湿度：" + bandData.getHumidity() + "%");
    }

    /**
     * 2016.8.30
     * 注册广播
     */
    private void registerLocalReceiver() {
        intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_BANDDATA);
        localReceiver = new LocalReceiver();
        localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);
    }

    private class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            bandData = (BandData) intent.getSerializableExtra("band_data");
            updateData(bandData);
        }
    }
}
