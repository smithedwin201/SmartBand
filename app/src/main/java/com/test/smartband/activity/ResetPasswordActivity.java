package com.test.smartband.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.test.smartband.BuildConfig;
import com.test.smartband.R;

/**
 * 重置密码
 */

public class ResetPasswordActivity extends AppCompatActivity {

    private ImageView backImageView;
    private EditText resetPwdEditText;
    private EditText ensurePwdEditText;
    private Button resetButton;
    private ImageView resetDeleteImageView;
    private ImageView ensurePwdDeleteImageView;
    private boolean isChangePwd;
    private LinearLayout changePwdLinearLayout;
    private EditText changePwdEditText;
    private ImageView changePwdDeleteImageView;
    private RelativeLayout changePwdTitleRela;
    private FrameLayout resetFrameLayout;
    private ImageView backImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        initView();
        setListener();
        //确定保存新密码
        resetPassword();

        Intent intent = getIntent();
        isChangePwd = intent.getBooleanExtra("change_pwd", false);
        if (isChangePwd) {
            resetFrameLayout.setVisibility(View.GONE);
            changePwdTitleRela.setVisibility(View.VISIBLE);//显示标题
            changePwdLinearLayout.setVisibility(View.VISIBLE);//显示旧密码输入框
            resetPwdEditText.setHint("请输入6~20位新密码");
            ensurePwdEditText.setHint("请输入确认新密码");
            resetButton.setText("提交");
        }
    }

    private void resetPassword() {
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //上传密码到服务器
                if (isChangePwd) {

                } else {

                }
            }
        });
    }

    private void setListener() {
        //返回上一界面
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //隐藏图标：对旧密码输入框进行监听，如果输入框失去焦点就隐藏删除图标
        changePwdEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && !changePwdEditText.getText().toString().equals("")) {
                    changePwdDeleteImageView.setVisibility(View.VISIBLE);
                } else {
                    changePwdDeleteImageView.setVisibility(View.GONE);
                }
            }
        });

        //隐藏图标：对密码输入框进行监听，如果输入框失去焦点就隐藏删除图标
        resetPwdEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && !resetPwdEditText.getText().toString().equals("")) {
                    resetDeleteImageView.setVisibility(View.VISIBLE);
                } else {
                    resetDeleteImageView.setVisibility(View.GONE);
                }
            }
        });
        //隐藏图标：对密码确认输入框进行监听，如果输入框失去焦点就隐藏删除图标
        ensurePwdEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && !ensurePwdEditText.getText().toString().equals("")) {
                    ensurePwdDeleteImageView.setVisibility(View.VISIBLE);
                } else {
                    ensurePwdDeleteImageView.setVisibility(View.GONE);
                }
            }
        });


        //显示图标：当旧密码输入框有输入内容的时候，显示删除图标
        changePwdEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!changePwdEditText.getText().toString().equals("")) {
                    changePwdDeleteImageView.setVisibility(View.VISIBLE);
                } else {
                    changePwdDeleteImageView.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //显示图标：当密码输入框有输入内容的时候，显示删除图标
        resetPwdEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!resetPwdEditText.getText().toString().equals("")) {
                    resetDeleteImageView.setVisibility(View.VISIBLE);
                } else {
                    resetDeleteImageView.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //显示图标：当密码确认输入框有输入内容的时候，显示删除图标
        ensurePwdEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!ensurePwdEditText.getText().toString().equals("")) {
                    ensurePwdDeleteImageView.setVisibility(View.VISIBLE);
                } else {
                    ensurePwdDeleteImageView.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //点击删除图标，删除旧密码框内所有内容
        changePwdDeleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePwdEditText.setText("");
            }
        });

        //点击删除图标，删除密码框内所有内容
        resetDeleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPwdEditText.setText("");
            }
        });
        //点击删除图标，删确认密码框内所有内容
        ensurePwdDeleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ensurePwdEditText.setText("");
            }
        });

    }

    private void initView() {
        resetFrameLayout = (FrameLayout) findViewById(R.id.fl_reset_pwd);
        backImageView = (ImageView) findViewById(R.id.resetPwd_back_imageView);
        resetPwdEditText = (EditText) findViewById(R.id.resetPwdEditText);
        ensurePwdEditText = (EditText) findViewById(R.id.ensurePwdEditText);
        resetButton = (Button) findViewById(R.id.resetPwd_button);
        //删除图标
        resetDeleteImageView = (ImageView) findViewById(R.id.delete_reset_password_imageview);
        ensurePwdDeleteImageView = (ImageView) findViewById(R.id.delete_ensure_password_imageview);
        changePwdDeleteImageView = (ImageView) findViewById(R.id.delete_old_password_imageview);
        //修改密码的控件
        backImage = (ImageView) findViewById(R.id.iv_back);
        changePwdTitleRela = (RelativeLayout) findViewById(R.id.re_change_pwd);
        changePwdLinearLayout = (LinearLayout) findViewById(R.id.change_pwd_linear_layout);
        changePwdEditText = (EditText) findViewById(R.id.changeOldPwdEditText);

    }

    /**
     * 当输入账号和密码时，点击账号或密码以外的区域就隐藏软键盘
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (null != this.getCurrentFocus()) {
                resetDeleteImageView.setVisibility(View.GONE);
                ensurePwdDeleteImageView.setVisibility(View.GONE);
                changePwdDeleteImageView.setVisibility(View.GONE);
                /**
                 * 点击空白位置 隐藏软键盘
                 */
                InputMethodManager mInputMethodManager =
                        (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                return mInputMethodManager.hideSoftInputFromWindow(
                        this.getCurrentFocus().getWindowToken(), 0);
            }
        }
        return super.onTouchEvent(event);
    }
}
