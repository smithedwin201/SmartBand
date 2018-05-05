package com.test.smartband.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.test.smartband.R;
import com.test.smartband.tools.CacheUtils;
import com.test.smartband.view.BottomPushPopupWindow;

public class WeightTargetActivity extends AppCompatActivity {

    private RelativeLayout weightTargetRelativeLayout;
    private Context mContext;
    private ImageView backImageView;
    private TextView weightTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_target);
        mContext = this;
        initView();

        weightTargetRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CacheUtils.putString(mContext, CacheUtils.WEIGHT_TARGET, 65 + "");
                new BottomPopupWeight(mContext).show(WeightTargetActivity.this);
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

        weightTextView = (TextView) findViewById(R.id.tv_weight_target);
        weightTargetRelativeLayout = (RelativeLayout) findViewById(R.id.re_weight_target);
        backImageView = (ImageView) findViewById(R.id.iv_back);
        String weight = CacheUtils.getString(mContext, CacheUtils.WEIGHT_TARGET, 60 + "");
        weightTextView.setText("目标体重：" + weight + " Kg");
    }

    /**
     * 步数选择底部弹出框
     */
    private class BottomPopupWeight extends BottomPushPopupWindow<Void> {

        public BottomPopupWeight(Context context) {
            super(context, null);
        }

        @Override
        protected View generateCustomView(Void data) {
            View root = View.inflate(context, R.layout.popup_weight_target, null);
            Button cancelButton = (Button) root.findViewById(R.id.cancel_weight);
            Button okButton = (Button) root.findViewById(R.id.ok_weight);
            final NumberPicker numberPicker = (NumberPicker) root.findViewById(R.id.number_picker_weight);
            Button customWeightTargetButton = (Button) root.findViewById(R.id.btn_custom_weight_target);

            final String[] strings = new String[25];
            int number = 45;
            for (int i = 0; i < strings.length; i++) {
                strings[i] = number + "";
                number += 1;
            }
            numberPicker.setDisplayedValues(strings);
            numberPicker.setMinValue(0);
            numberPicker.setMaxValue(strings.length - 1);
            numberPicker.setValue(10);

            customWeightTargetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    View view = LayoutInflater.from(mContext)
                            .inflate(R.layout.dialog_set_number, null);
                    TextView titleText = (TextView) view.findViewById(R.id.title_text);
                    titleText.setText("请输入目标体重（Kg）");
                    final EditText weightTargetEditText = (EditText) view.findViewById(R.id.et_set_number);
                    weightTargetEditText.setHint("60");
                    //设置输入长度为2，体重设置最大是两位数
                    weightTargetEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(2)});
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setView(view);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String weight = weightTargetEditText.getText().toString().trim();
                            weightTextView.setText("目标体重：" + weight + " Kg");
                            CacheUtils.putString(mContext, CacheUtils.WEIGHT_TARGET, weight);
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
                    weightTextView.setText("目标体重：" + strings[value] + " Kg");
                    CacheUtils.putString(mContext, CacheUtils.WEIGHT_TARGET, strings[value]);
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

