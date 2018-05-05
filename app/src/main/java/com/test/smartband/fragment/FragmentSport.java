package com.test.smartband.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.test.smartband.R;
import com.test.smartband.activity.TodaySleepActivity;
import com.test.smartband.activity.TodaySportActivity;
import com.test.smartband.model.BandData;
import com.test.smartband.service.BluetoothLeService;
import com.test.smartband.view.ProcessView;

/**
 * Created by chuangfeng on 2016/4/24.
 */
public class FragmentSport extends Fragment {

    private View view;
    public static ProcessView mProcessView;
    public static TextView mStepCount;
    public static TextView mCalCount;
    private Button btn_addprogress;
    private int counter = 10;
    private ImageView iv_sportmode;
    private ImageView iv_sleepmode;
    private LinearLayout sportLayout;
    private LinearLayout sleepLayout;
    private IntentFilter intentFilter;
    private LocalReceiver localReceiver;
    private BandData bandData;
    private int progress;
    private int currentMode = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sport,container,false);
        initView();
        registerLocalReceiver();
        Log.e("FragmentSport", "onCreateView");

        return view;
    }

    public void onResume() {
        super.onResume();
        Log.e("FragmentSport", "onResume");
    }

    private void initView() {

        mProcessView = (ProcessView) view.findViewById(R.id.process_view);
        mStepCount = (TextView) view.findViewById(R.id.step_count);
        mCalCount = (TextView) view.findViewById(R.id.cal_count);
        sportLayout = (LinearLayout)view.findViewById(R.id.sport_layout);
        sleepLayout = (LinearLayout)view.findViewById(R.id.sleep_layout);
        iv_sportmode = (ImageView) view.findViewById(R.id.iv_sport_mode);
        iv_sportmode.setSelected(true);
        iv_sportmode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_sleepmode.setSelected(false);
                iv_sportmode.setSelected(true);
                sportLayout.setVisibility(View.VISIBLE);
                sleepLayout.setVisibility(View.GONE);
                mProcessView.changeMode(ProcessView.SHOWSPORTMODE);
                mProcessView.setProgress(progress);
                currentMode = ProcessView.SHOWSPORTMODE;
            }
        });
        iv_sleepmode = (ImageView) view.findViewById(R.id.iv_sleep_mode);
        iv_sleepmode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_sportmode.setSelected  (false);
                iv_sleepmode.setSelected(true);
                sportLayout.setVisibility(View.GONE);
                sleepLayout.setVisibility(View.VISIBLE);
                mProcessView.changeMode(ProcessView.SHOWSLEEPMODE);
                mProcessView.setProgress(70);
                currentMode = ProcessView.SHOWSLEEPMODE;
            }
        });

        mProcessView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentMode == ProcessView.SHOWSPORTMODE) {
                    Intent intent = new Intent(getActivity(), TodaySportActivity.class);
                    startActivity(intent);
                } else if (currentMode == ProcessView.SHOWSLEEPMODE) {
                    Intent intent = new Intent(getActivity(), TodaySleepActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void updateData(BandData bandData) {
        mStepCount.setText(bandData.getStep()+"");
        progress = bandData.getStep() * 100 / 1000;
        if (ProcessView.mode == 0) {
            mProcessView.setProgress(progress);//1000是用户设置的目标值
        }
        mCalCount.setText(bandData.getCalorie()+"");
    }

    /**
     * 2016.8.28
     * 注册广播
     */
    private void registerLocalReceiver() {
        intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_BANDDATA);
        localReceiver = new LocalReceiver();
        getActivity().registerReceiver(localReceiver, intentFilter);
    }
    private class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            bandData = (BandData) intent.getSerializableExtra("band_data");
            updateData(bandData);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e("FragmentSport", "onDestroyView");

        getActivity().unregisterReceiver(localReceiver);
    }
}
