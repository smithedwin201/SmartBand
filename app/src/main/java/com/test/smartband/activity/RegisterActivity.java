package com.test.smartband.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mob.MobSDK;
import com.test.smartband.R;
import com.test.smartband.net.HttpUtil;
import com.test.smartband.tools.CacheUtils;
import com.test.smartband.tools.Config;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import okhttp3.Call;
import okhttp3.Response;


/**
 * 注册新账号
 */

public class RegisterActivity extends BaseActivity {

    private ImageView backImageView;
    private EditText phoneNumEditText;
    private EditText ensurePwdEditText;
    private EditText registerPwdEditText;
    private EditText verificationCode;
    private Button registerButton;
    private Button getCodeButton;
    private TextView userAgreementTextView;
    private ImageView deletePhoneNumImageView;
    private ImageView deletePwdImageView;
    private ImageView deleteEnsurePwdImageView;
    private ImageView warningEnsurePwdImageView;
    private ImageView warningPwdImageView;
    private InputMethodManager mManager;
    private int i = 60;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initSDK();
        initView();
        setListener();
    }

    private void setListener() {
        //返回上一界面
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //获取验证码
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
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //第一次输入密码的检查
                if(registerPwdEditText.length()<6 && !registerPwdEditText.getText().toString().equals("")){
                    warningPwdImageView.setVisibility(View.VISIBLE);
                }else{warningPwdImageView.setVisibility(View.GONE);}

                //确认密码框的输入检查
                if(!ensurePwdEditText.getText().toString().equals(registerPwdEditText.getText().
                        toString()) && !ensurePwdEditText.getText().toString().equals("")){
                    warningEnsurePwdImageView.setVisibility(View.VISIBLE);
                }else{ warningEnsurePwdImageView.setVisibility(View.GONE);}

                //各种检测成功后才能执行注册
                if((warningPwdImageView.getVisibility() == View.GONE )
                        &&(warningEnsurePwdImageView.getVisibility() == View.GONE )
                        &&!registerPwdEditText.getText().toString().equals("")
                        &&!phoneNumEditText.getText().toString().equals("")
                        &&!ensurePwdEditText.getText().toString().equals("")
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

        //当输入框正在输入，就显示全部删除图标
        phoneNumEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && !phoneNumEditText.getText().toString().equals("")) {
                    deletePhoneNumImageView.setVisibility(View.VISIBLE);
                }else {
                    deletePhoneNumImageView.setVisibility(View.GONE);
                }
            }
        });

        //当输入框正在输入，就显示全部删除图标
        registerPwdEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && !registerPwdEditText.getText().toString().equals("")) {
                    deletePwdImageView.setVisibility(View.VISIBLE);
                } else {
                    deletePwdImageView.setVisibility(View.GONE);
                    //此处以后可以更换为显示感叹号图标
                    if(registerPwdEditText.length()<6 && !registerPwdEditText.getText().toString().equals("")){
                        Toast.makeText(getApplicationContext(), "密码长度小于6，请重新输入",Toast.LENGTH_SHORT).show();
                        warningPwdImageView.setVisibility(View.VISIBLE);
                    }else{
                        warningPwdImageView.setVisibility(View.GONE);
                    }
                }
            }
        });

        //当输入框正在输入，就显示全部删除图标
        ensurePwdEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && !ensurePwdEditText.getText().toString().equals("")) {
                    deleteEnsurePwdImageView.setVisibility(View.VISIBLE);
                } else {
                    deleteEnsurePwdImageView.setVisibility(View.GONE);
                    //此处以后可以更换为显示感叹号图标
                    if(!ensurePwdEditText.getText().toString().equals(registerPwdEditText.getText().
                            toString()) && !ensurePwdEditText.getText().toString().equals("")){
                        Toast.makeText(getApplicationContext(), "两次输入的密码不同，请重新输入！",Toast.LENGTH_SHORT).show();
                        warningEnsurePwdImageView.setVisibility(View.VISIBLE);
                    }else{
                        warningEnsurePwdImageView.setVisibility(View.GONE);
                    }
                }
            }
        });
        userAgreementTextView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this,
                        UserAgreementActivity.class);
                startActivity(intent);
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
            public void afterTextChanged(Editable s) {
            }
        });

        registerPwdEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!registerPwdEditText.getText().toString().equals("")) {
                    deletePwdImageView.setVisibility(View.VISIBLE);
                } else {
                    deletePwdImageView.setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        ensurePwdEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!ensurePwdEditText.getText().toString().equals("")) {
                    deleteEnsurePwdImageView.setVisibility(View.VISIBLE);
                } else {
                    deleteEnsurePwdImageView.setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        //点击删除图标，删除输入框所有内容
        deletePhoneNumImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumEditText.setText("");
            }
        });
        deletePwdImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerPwdEditText.setText("");
            }
        });
        deleteEnsurePwdImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ensurePwdEditText.setText("");
            }
        });

    }

    private void initView() {
        backImageView = (ImageView) findViewById(R.id.register_back_imageView);
        phoneNumEditText = (EditText) findViewById(R.id.phoneEditText);
        registerPwdEditText = (EditText) findViewById(R.id.registerPwdEditText);
        ensurePwdEditText = (EditText) findViewById(R.id.ensurePwdEditText);
        verificationCode = (EditText) findViewById(R.id.codeEditText);
        getCodeButton = (Button) findViewById(R.id.get_code_button);
        registerButton = (Button) findViewById(R.id.register_button);
        userAgreementTextView = (TextView) findViewById(R.id.user_agreement_text);
        deletePhoneNumImageView = (ImageView) findViewById(R.id.delete_register_number_imageview);
        deletePwdImageView = (ImageView) findViewById(R.id.delete_register_password_imageview);
        deleteEnsurePwdImageView = (ImageView) findViewById(R.id.delete_ensure_password_imageview);
        warningPwdImageView=(ImageView) findViewById(R.id.warning_register_password_imageview);
        warningEnsurePwdImageView = (ImageView) findViewById(R.id.warning_ensure_password_imageview);
    }

    /**
     * 当输入账号和密码时，点击账号或密码以外的区域就隐藏软键盘
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if(null != this.getCurrentFocus()){
                deletePhoneNumImageView.setVisibility(View.GONE);
                deletePwdImageView.setVisibility(View.GONE);
                deleteEnsurePwdImageView.setVisibility(View.GONE);
                if(registerPwdEditText.length()<6 && !registerPwdEditText.getText().toString().equals("")){
                    warningPwdImageView.setVisibility(View.VISIBLE);
                }
                if(!ensurePwdEditText.getText().toString().equals(registerPwdEditText.getText().
                        toString()) && !ensurePwdEditText.getText().toString().equals("")){
                    warningEnsurePwdImageView.setVisibility(View.VISIBLE);
                }
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


    private void initSDK() {
        //MobSDK.init(this, "1f484daddb3d6", "ef58f13641c76b66cfdb866041bfac71");//574223680
       // MobSDK.init(this, "1f497d3348b93", "5c28964c713849441d0cf3727673f667");//591803511可以的
        MobSDK.init(this, "1f484daddb3d6", "ef58f13641c76b66cfdb866041bfac71");
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

                        HttpUtil.Register(phoneNumEditText.getText().toString(),ensurePwdEditText.getText().toString(),
                                new okhttp3.Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                Toast.makeText(RegisterActivity.this,
                                                        R.string.fail_to_login, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        String responseText = response.body().string();
                                        Log.e("LoginActivity", responseText);
                                        try {
                                            if (responseText.equals("")) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        Toast.makeText(RegisterActivity.this,
                                                                R.string.fail_to_login, Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                                return;
                                            }
                                            JSONObject object = new JSONObject(responseText);
                                            if (object.getInt(Config.KEY_RegisterSTATUS) == 1) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Config.setUser(phoneNumEditText.getText().toString());
                                                        Config.setPassword(ensurePwdEditText.getText().toString());
                                                        Toast.makeText(RegisterActivity.this,
                                                                "从服务器验证账号密码方式注册成功", Toast.LENGTH_SHORT).show();
                                                        //记录用户已经登录了
                                                        CacheUtils.putBoolean(RegisterActivity.this, CacheUtils.IS_LOGIN, true);
                                                        Intent intent = new Intent(RegisterActivity.this, MainBleActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                });
                                            }else{
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        Toast.makeText(RegisterActivity.this,
                                                                "没有从服务器验证账号密码方式注册成功", Toast.LENGTH_SHORT).show();
                                                        //记录用户已经登录了
//                                                CacheUtils.putBoolean(LoginActivity.this, CacheUtils.IS_LOGIN, true);
//                                                Intent intent = new Intent(LoginActivity.this, MainBleActivity.class);
//                                                startActivity(intent);
//                                                finish();
                                                    }
                                                });


                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });


                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        Toast.makeText(getApplicationContext(), "验证码已经发送",
                                Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getApplicationContext(), "你输入的验证码有误",
                            Toast.LENGTH_SHORT).show();
                    ((Throwable) data).printStackTrace();
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

    private void createProgressBar() {
        FrameLayout layout = (FrameLayout) findViewById(android.R.id.content);
        LayoutParams layoutParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        ProgressBar mProBar = new ProgressBar(this);
        mProBar.setLayoutParams(layoutParams);
        mProBar.setVisibility(View.VISIBLE);
        layout.addView(mProBar);
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

    @Override
    protected void onDestroy() {

        //this.unregisterReceiver(smsBroadcastReceiver);
        SMSSDK.unregisterAllEventHandler();
        super.onDestroy();
    }
}
