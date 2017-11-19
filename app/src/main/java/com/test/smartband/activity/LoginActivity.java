package com.test.smartband.activity;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.test.smartband.R;
import com.test.smartband.net.HttpUtil;
import com.test.smartband.tools.CacheUtils;
import com.test.smartband.tools.Config;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 登录
 */
public class LoginActivity extends BaseActivity {

    private EditText userEditText;
    private EditText pwdEditText;
    private Button loginButton;
    private ImageView deleteNumber;
    private ImageView deletePassword;
    private InputMethodManager mManager;
    private TextView forgetPwdTextView;
    private TextView registerTextView;
    private ImageView ciphertextImageView;
    private ImageView plaintextImageView;
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //如果没有退出用户账号，就直接跳转到主界面，否则显示登录界面
        if (CacheUtils.getBoolean(this, CacheUtils.IS_LOGIN, false)) {
            Intent intent = new Intent(LoginActivity.this, MainBleActivity.class);
            startActivity(intent);
        }
        initView();
        login();

    }

    private void login() {

        //点击登录按钮进行登录
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (TextUtils.isEmpty(userEditText.getText().toString())) {
//                    Toast.makeText(LoginActivity.this,
//                            R.string.phone_num_cannot_be_empty, Toast.LENGTH_LONG).show();
//                    return;
//                } else {
//                    if (!judgePhoneNums(userEditText.getText().toString())) {
//                        if (userEditText.getText().toString().equals("123")) {
//                            Toast.makeText(LoginActivity.this,
//                                    "使用本地账号登录", Toast.LENGTH_LONG).show();
//                        } else {
//                            Toast.makeText(LoginActivity.this,
//                                    "手机号码输入有误", Toast.LENGTH_LONG).show();
//                            return;
//                        }
//                    }
//                }
//
//                if (TextUtils.isEmpty(pwdEditText.getText().toString())) {
//                    Toast.makeText(LoginActivity.this,
//                            R.string.code_cannot_be_empty, Toast.LENGTH_SHORT).show();
//                    return;
//                }

//                final ProgressDialog progressDialog = ProgressDialog.show(
//                        LoginActivity.this,
//                        getResources().getString(R.string.connecting),
//                        getResources().getString(R.string.connecting_to_server));

//                /**
//                 * 用于测试先写一个本地账号进行登录，后期要删除这段代码
//                 */
//                if (userEditText.getText().toString().equals("123456")
//                        && pwdEditText.getText().toString().equals("admin")) {
//
//                    //记录用户已经登录了
//                    CacheUtils.putBoolean(LoginActivity.this, CacheUtils.IS_LOGIN, true);
//                    Intent intent = new Intent(LoginActivity.this, MainBleActivity.class);
//                    startActivity(intent);
//                    finish();
//                }

                HttpUtil.login(userEditText.getText().toString(), pwdEditText.getText().toString(),
                        new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        Toast.makeText(LoginActivity.this,
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

                                                Toast.makeText(LoginActivity.this,
                                                        R.string.fail_to_login, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        return;
                                    }
                                    JSONObject object = new JSONObject(responseText);
                                    if (object.getInt(Config.KEY_STATUS) == 1) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Config.setPassword(pwdEditText.getText().toString());
                                                Config.setUser(userEditText.getText().toString());
                                                Toast.makeText(LoginActivity.this,
                                                        "从服务器验证账号密码方式登录成功", Toast.LENGTH_SHORT).show();
                                                //记录用户已经登录了
                                                CacheUtils.putBoolean(LoginActivity.this, CacheUtils.IS_LOGIN, true);
                                                Intent intent = new Intent(LoginActivity.this, MainBleActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        });
                                    }else{
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                Toast.makeText(LoginActivity.this,
                                                        "没有从服务器验证账号密码方式登录成功", Toast.LENGTH_SHORT).show();
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

//                new Login(userEditText.getText().toString(), pwdEditText.getText().toString(),
//                        new Login.SuccessCallback() {
//                            @Override
//                            public void onSuccess() {
//                                progressDialog.dismiss();
//                                //记录用户已经登录了
//                                CacheUtils.putBoolean(LoginActivity.this, CacheUtils.IS_LOGIN, true);
//                                Intent intent = new Intent(LoginActivity.this, MainBleActivity.class);
//                                startActivity(intent);
//                                finish();
//                            }
////                            @Override
////                            public void onSuccess(String token) {
////                                progressDialog.dismiss();
////                                Config.cacheToken(LoginActivity.this, token);
////                                Config.cachePhoneNum(LoginActivity.this, userEditText.getText().toString());
////
////                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
////                                intent.putExtra(Config.KEY_TOKEN, token);
////                                intent.putExtra(Config.KEY_PHONE_NUM, userEditText.getText().toString());
////                                startActivity(intent);
////
////                                finish();
////                            }
//                        }, new Login.FailCallback() {
//                    @Override
//                    public void onFail() {
//                        progressDialog.dismiss();
//                        Toast.makeText(LoginActivity.this,
//                                R.string.fail_to_login, Toast.LENGTH_SHORT).show();
//                    }
//                });
            }
        });

        //删除所有手机号的输入
        deleteNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userEditText.setText("");
            }
        });

        //删除所有密码的输入
        deletePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pwdEditText.setText("");
            }
        });

        //进行明文和密码的转换
        plaintextImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (plaintextImageView.getVisibility() == View.VISIBLE) {
                    plaintextImageView.setVisibility(View.GONE);
                    ciphertextImageView.setVisibility(View.VISIBLE);
                    //密码不可见
                    pwdEditText.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    plaintextImageView.setVisibility(View.VISIBLE);
                    ciphertextImageView.setVisibility(View.GONE);
                    //密码可见
                    pwdEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                //明文和密码转换后，把光标移到最后
                CharSequence text = pwdEditText.getText();
                if (text instanceof Spannable) {
                    Spannable spanText = (Spannable) text;
                    Selection.setSelection(spanText, text.length());// 将光标移动到最后
                }
            }
        });

        //进行明文和密码的转换
        ciphertextImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ciphertextImageView.getVisibility() == View.VISIBLE) {
                    plaintextImageView.setVisibility(View.VISIBLE);
                    ciphertextImageView.setVisibility(View.GONE);
                    //密码可见
                    pwdEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    plaintextImageView.setVisibility(View.GONE);
                    ciphertextImageView.setVisibility(View.VISIBLE);
                    //密码不可见
                    pwdEditText.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }

                //明文和密码转换后，把光标移到最后
                CharSequence text = pwdEditText.getText();
                if (text instanceof Spannable) {
                    Spannable spanText = (Spannable) text;
                    Selection.setSelection(spanText, text.length());// 将光标移动到最后
                }
            }
        });

        forgetPwdTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });

        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        /**
         * 用来检测用户输入时，显示删除的图标
         */
        userEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && !userEditText.getText().toString().equals("")) {
                    deleteNumber.setVisibility(View.VISIBLE);
                } else {
                    deleteNumber.setVisibility(View.GONE);
                }
            }
        });

        pwdEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && !pwdEditText.getText().toString().equals("")) {
                    deletePassword.setVisibility(View.VISIBLE);
                } else {
                    deletePassword.setVisibility(View.GONE);
                }
            }
        });

    }

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
        //Toast.makeText(this, "手机号码输入有误！",Toast.LENGTH_SHORT).show();
        return false;
    }

    /**
     * 判断一个字符串的位数
     *
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

    private void initView() {
        userEditText = (EditText) findViewById(R.id.userEditText);
        pwdEditText = (EditText) findViewById(R.id.pwdEditText);
        loginButton = (Button) findViewById(R.id.btn_login);
        deleteNumber = (ImageView) findViewById(R.id.delete_number_imageview);
        deletePassword = (ImageView) findViewById(R.id.delete_password_imageview);
        forgetPwdTextView = (TextView) findViewById(R.id.tv_forgetPassWord);
        registerTextView = (TextView) findViewById(R.id.tv_register);
        ciphertextImageView = (ImageView) findViewById(R.id.ciphertext_imageview);
        plaintextImageView = (ImageView) findViewById(R.id.plaintext_imageview);
        userEditText.addTextChangedListener(userEditListener);
        pwdEditText.addTextChangedListener(pwdEditListener);

        //获取软键盘的服务管理
        mManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    /**
     * 用来检测用户输入时，显示删除的图标
     */
    TextWatcher userEditListener = new TextWatcher() {

        //文本变化前，s:之前的文字内容，start:添加文字的位置(从0开始)，count:不知道 一直是0，after:添加的文字总数
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        //文本变化时，s:之后的文字内容，start:添加文字的位置(从0开始)，before:不知道 一直是0，count:添加的文字总数
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!userEditText.getText().toString().equals("")) {
                deleteNumber.setVisibility(View.VISIBLE);
            } else {
                deleteNumber.setVisibility(View.GONE);
            }
        }

        //文本变化后，s:之后的文字内容
        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    TextWatcher pwdEditListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!pwdEditText.getText().toString().equals("")) {
                deletePassword.setVisibility(View.VISIBLE);
            } else {
                deletePassword.setVisibility(View.GONE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    /**
     * 当输入账号和密码时，点击账号或密码以外的区域就隐藏输入键盘
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (null != this.getCurrentFocus()) {
                mManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                deleteNumber.setVisibility(View.GONE);
                deletePassword.setVisibility(View.GONE);
                /**
                 * 点击空白位置 隐藏软键盘
                 */
                InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
            }
        }
        return super.onTouchEvent(event);
    }

    //    @Override
//    public void onBackPressed() {
//
//        if ((System.currentTimeMillis() - exitTime) > 2000) {
//            Toast.makeText(getApplicationContext(), "再按一次退出程序",
//                    Toast.LENGTH_SHORT).show();
//            exitTime = System.currentTimeMillis();
//        } else {
//        }
//    }
    public void onBackPressed() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("退出提示");
        dialog.setMessage("您还未登录，是否要退出手环程序？");
        dialog.setCancelable(false);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Log.e("woshishei", "onba " );
                //                mBluetoothAdapter.disable();//关闭蓝牙
                ActivityCollector.finishAll();
                android.os.Process.killProcess(android.os.Process.myPid());
//                System.exit(0);


            }
        });
        dialog.setNegativeButton("取消", null);
        dialog.create();
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("LoginActivity", "onResume: ");
    }
    @Override
    protected void onDestroy() {
        Log.e("LoginActivity", "onDestroy");
        super.onDestroy();

    }
}

