package com.test.smartband.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import com.test.smartband.R;
import com.test.smartband.tools.CacheUtils;

/**
 * 跑步设置
 */
public class RunSettingActivity extends AppCompatActivity {

    private ImageView backImage;
    private Switch voiceSettingSwitch;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_setting);
        mContext = this;

        initView();
        addListener();
    }

    private void addListener() {
        RunSettingListener listener = new RunSettingListener();
        backImage.setOnClickListener(listener);

        voiceSettingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //保存是否开启语音播放
                if (isChecked) {
                    CacheUtils.putBoolean(mContext, CacheUtils.IS_OPEN_RUN_SOUND, true);
                } else {
                    CacheUtils.putBoolean(mContext, CacheUtils.IS_OPEN_RUN_SOUND, false);
                }
            }
        });
    }

    private void initView() {
        backImage = (ImageView) findViewById(R.id.iv_back);
        voiceSettingSwitch = (Switch) findViewById(R.id.switch_voice_setting);

        if (CacheUtils.getBoolean(mContext, CacheUtils.IS_OPEN_RUN_SOUND, true)) {
            voiceSettingSwitch.setChecked(true);
        } else {
            voiceSettingSwitch.setChecked(false);
        }

    }

    class RunSettingListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_back:
                    finish();
                    break;
                default:
                    break;
            }
        }
    }
}
