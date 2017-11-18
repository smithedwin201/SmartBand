package com.test.smartband.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mob.MobSDK;
import com.test.smartband.R;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * 忘记密码
 */

public class ForgetPasswordActivity extends AppCompatActivity {

    private ImageView backImageView;
    private EditText phoneNumEditText;
    private EditText verificationCode;
    private Button getCodeButton;
    private ImageView deletePhoneNumImageView;
    private Button confirmButton;
    private TextView titleTextView;
    private int i = 60;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        initView();
        setListener();
        initSDK();
    }

    private void setListener() {

        //返回上一界面
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        phoneNumEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!phoneNumEditText.getText().toString().equals("")) {
                    deletePhoneNumImageView.setVisibility(View.VISIBLE);
                }else {
                    deletePhoneNumImageView.setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

//        //点击获取验证码
//        getCodeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                CountDownTimerUtils mCountDownTimerUtils = new CountDownTimerUtils(
//                        ForgetPasswordActivity.this, getCodeButton, 60000, 1000);
//                mCountDownTimerUtils.start();
//            }
//        });

        //点击删除图标，删除输入框所有内容
        deletePhoneNumImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumEditText.setText("");
            }
        });


        getCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = 60;
                String phoneNums = phoneNumEditText.getText().toString();
                // 1. 通过规则判断手机号
                if (!judgePhoneNums(phoneNums)) {
                    return;
                } // 2. 通过sdk发送短信验证

                if( isNetworkConnected(getApplicationContext()) ){
                    SMSSDK.getVerificationCode("86",phoneNums);//请求获取短信验证码，在监听中EvenHandler接口返回，phoneNums:手机号
                    //SMSSDK.getVerificationCode("86", phoneNums);

                    // 3. 把按钮变成不可点击，并且显示倒计时（正在获取）
                    getCodeButton.setClickable(false);
                    getCodeButton.setText("重新发送(" + i-- + ")");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 60; i > 0; i--) {
                                handler.sendEmptyMessage(-9);
                                if (i <= 0) {
                                    break;
                                }
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            handler.sendEmptyMessage(-8);
                        }
                    }).start();
                }else{
                    Toast.makeText(getApplication(), "请检查网络连接", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //注册
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //各种检测成功后才能执行注册
                if(!phoneNumEditText.getText().toString().equals("")
                        &&!verificationCode.getText().toString().equals("")) {
                    //检查验证码框是否有内容
                    if(!verificationCode.getText().toString().equals("")){
                        String phoneNums = phoneNumEditText.getText().toString();
                        //判断手机号码格式是否正确，正确则执行接受验证码
                        if(judgePhoneNums(phoneNums)){
                            SMSSDK.submitVerificationCode("86", phoneNums, verificationCode
                                    .getText().toString());
                            //createProgressBar();
                        }
                        // 验证通过之后，将smssdk注册代码注销
                        // SMSSDK.unregisterEventHandler(eventHandler);
                    }else{
                        Toast.makeText(getApplicationContext(), "请输入验证码",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "你的输入有误，请检查",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void initView() {

        confirmButton = (Button) findViewById(R.id.confirm_button);
        backImageView = (ImageView) findViewById(R.id.forgetPwd_back_imageView);
        phoneNumEditText = (EditText) findViewById(R.id.phoneEditText);
        verificationCode = (EditText) findViewById(R.id.codeEditText);
        getCodeButton = (Button) findViewById(R.id.get_code_button);
        deletePhoneNumImageView = (ImageView) findViewById(R.id.delete_register_number_imageview);
    }


    /**
     * 当输点击空白区域就隐藏软键盘
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if(null != this.getCurrentFocus()){
                deletePhoneNumImageView.setVisibility(View.GONE);
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

    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }


    /**
     * 以下内容为短信验证等的代码
     */
    private void initSDK() {
        //MobSDK.init(this, "1f484daddb3d6", "ef58f13641c76b66cfdb866041bfac71");//574223680
        MobSDK.init(this, "1f497d3348b93", "5c28964c713849441d0cf3727673f667");//591803511
        EventHandler eventHandler = new EventHandler() {
            /**
             * 在操作之后被触发
             *
             * @param event
             *            参数1
             * @param result
             *            参数2 SMSSDK.RESULT_COMPLETE表示操作成功，为SMSSDK.
             *            RESULT_ERROR表示操作失败
             * @param data
             *            事件操作的结果
             */
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
//                if (data instanceof Throwable) {
//                    Throwable throwable = (Throwable)data;
//                    String msg = throwable.getMessage();
//                    Toast.makeText(getApplication(), msg, Toast.LENGTH_SHORT).show();
//                } else {
//                    if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
//                        // 处理你自己的逻辑
//                    }
//                }
            }
        };
        // 注册回调监听接口
        SMSSDK.registerEventHandler(eventHandler);
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == -9) {
                getCodeButton.setText("重新发送(" + i-- + ")");
            } else if (msg.what == -8) {
                getCodeButton.setText("获取验证码");
                getCodeButton.setClickable(true);
            } else {
                int event = msg.arg1;
                int result = msg.arg2;
                Object data = msg.obj;
                //Log.e("event", "event=" + event);
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // 短信注册成功后，返回MainActivity,然后提示新好友
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
                        Toast.makeText(getApplicationContext(), "提交验证码成功",
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ForgetPasswordActivity.this,
                                ResetPasswordActivity.class);
                        //phoneNumEditText.getText()
                        //registerPwdEditText.getText()
                        startActivity(intent);
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        Toast.makeText(getApplicationContext(), "验证码已经发送",
                                Toast.LENGTH_SHORT).show();
                    } else {
//                        Toast.makeText(getApplicationContext(), "你输入的验证码有误",
//                                Toast.LENGTH_SHORT).show();
                        ((Throwable) data).printStackTrace();
                    }
                }
            }
        }
    };

    /**
     * 判断手机号码是否合理
     *
     * @param phoneNums
     */
    private boolean judgePhoneNums(String phoneNums) {
        if (isMatchLength(phoneNums, 11)
                && isMobileNO(phoneNums)) {
            return true;
        }
        Toast.makeText(this, "手机号码输入有误！",Toast.LENGTH_SHORT).show();
        return false;
    }

    /**
     * 判断一个字符串的位数
     * @param str
     * @param length
     * @return
     */
    public static boolean isMatchLength(String str, int length) {
        if (str.isEmpty()) {
            return false;
        } else {
            return str.length() == length ? true : false;
        }
    }

    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mobileNums) {
		/*
		 * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
		 * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
		 * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		 */
        String telRegex = "[1][358]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobileNums))
            return false;
        else
            return mobileNums.matches(telRegex);
    }
}

