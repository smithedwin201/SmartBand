package com.test.smartband.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.test.smartband.R;
import com.test.smartband.tools.CacheUtils;
import com.test.smartband.view.BottomPushPopupWindow;

/**
 * 设置步数目标
 */
public class StepTargetNumberActivity extends AppCompatActivity {

    private RelativeLayout baseTargetRelativeLayout;
    private RadioButton baseTargetRadioButton;
    private RelativeLayout customTargetRelativeLayout;
    private TextView customTargetTextView;
    private RadioButton customTargetRadioButton;
    private Context mContext;
    private ImageView backImageView;
    private  String DEFAULT_TYPE = "default";//默认
    private String CUSTOM_TYPE = "custom";//自定义

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_target_number);
        mContext = this;
        initView();

        baseTargetRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                baseTargetRadioButton.setChecked(true);
                customTargetRadioButton.setChecked(false);
                CacheUtils.putString(mContext, CacheUtils.STEP_NUMBER_SETTING_TYPE, DEFAULT_TYPE);
                CacheUtils.putString(mContext, CacheUtils.STEP_TARGET_NUMBER, 8000 + "");
            }
        });

        customTargetRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                baseTargetRadioButton.setChecked(false);
                customTargetRadioButton.setChecked(true);
                CacheUtils.putString(mContext, CacheUtils.STEP_NUMBER_SETTING_TYPE, CUSTOM_TYPE);
                CacheUtils.putString(mContext, CacheUtils.STEP_TARGET_NUMBER, 5000 + "");
                new BottomPopupStepNumber(mContext).show(StepTargetNumberActivity.this);
            }
        });

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        baseTargetRelativeLayout = (RelativeLayout) findViewById(R.id.re_base_target);
        baseTargetRadioButton = (RadioButton) findViewById(R.id.rb_base_target);
        customTargetRelativeLayout = (RelativeLayout) findViewById(R.id.re_custom_target);
        customTargetTextView = (TextView) findViewById(R.id.tv_custom_target);
        customTargetRadioButton = (RadioButton) findViewById(R.id.rb_custom_target);
        backImageView = (ImageView) findViewById(R.id.iv_back);

        if (CacheUtils.getString(mContext, CacheUtils.STEP_NUMBER_SETTING_TYPE, DEFAULT_TYPE).equals(DEFAULT_TYPE)) {
            baseTargetRadioButton.setChecked(true);
            customTargetRadioButton.setChecked(false);
        }else {
            baseTargetRadioButton.setChecked(false);
            customTargetRadioButton.setChecked(true);
            String stepNumber = CacheUtils.getString(mContext, CacheUtils.STEP_TARGET_NUMBER, 5000 + "");
            customTargetTextView.setText("自定义目标：" + stepNumber);
        }
    }


    /**
     * 步数选择底部弹出框
     */
    private class BottomPopupStepNumber extends BottomPushPopupWindow<Void> {

        public BottomPopupStepNumber(Context context) {
            super(context, null);
        }

        @Override
        protected View generateCustomView(Void data) {
            View root = View.inflate(context, R.layout.popup_step_number_target, null);
            Button cancelButton = (Button) root.findViewById(R.id.cancel_step);
            Button okButton = (Button) root.findViewById(R.id.ok_step);
            final NumberPicker numberPicker = (NumberPicker) root.findViewById(R.id.number_picker_step);
            Button customStepNumberButton = (Button) root.findViewById(R.id.btn_custom_step_target);

            final String[] strings = new String[16];
            int number = 5000;
            for (int i = 0; i < strings.length; i++) {
                strings[i] = number + "";
                number += 1000;
            }
            numberPicker.setDisplayedValues(strings);
            numberPicker.setMinValue(0);
            numberPicker.setMaxValue(strings.length - 1);
            numberPicker.setValue(5);

            customStepNumberButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    View view = LayoutInflater.from(mContext)
                            .inflate(R.layout.dialog_set_number, null);
                    final EditText stepNumberEditText = (EditText) view.findViewById(R.id.et_set_number);
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setView(view);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String step = stepNumberEditText.getText().toString().trim();
                            customTargetTextView.setText("自定义目标："+ step);
                            CacheUtils.putString(mContext, CacheUtils.STEP_TARGET_NUMBER, step);
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton("取消", null);
                    builder.create();
                    builder.show();
                }
            });

            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int value = numberPicker.getValue();
                    customTargetTextView.setText("自定义目标："+ strings[value]);
                    CacheUtils.putString(mContext, CacheUtils.STEP_TARGET_NUMBER, strings[value]);
                    dismiss();
                }
            });
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            return root;
        }
    }
}

