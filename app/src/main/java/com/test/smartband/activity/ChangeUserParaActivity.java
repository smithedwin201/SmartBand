
package com.test.smartband.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.test.smartband.R;
import com.test.smartband.other.LocalUserInfo;

import java.util.Timer;
import java.util.TimerTask;

public class ChangeUserParaActivity extends Activity {

    private TextView tv_title;
    private TextView tv_tips;
    private TextView tv_unit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user_para);
        Intent intent = getIntent();
        final String content = intent.getStringExtra("content");
        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_tips = (TextView)findViewById(R.id.tips);
        tv_unit = (TextView)findViewById(R.id.tv_unit);

        final String old_content = LocalUserInfo.getInstance(ChangeUserParaActivity.this).getUserInfo(content);
        final EditText et_content = (EditText) this.findViewById(R.id.et_content);
        et_content.setText(old_content);

        if (content.equals("height")) {
            tv_title.setText("更改身高");
            tv_tips.setText("请输入你的身高");
            tv_unit.setVisibility(View.VISIBLE);
        } else if (content.equals("weight")) {
            tv_title.setText("更改体重");
            tv_tips.setText("请输入你的体重");
            tv_unit.setText("Kg");
            tv_unit.setVisibility(View.VISIBLE);
        } else {
            et_content.setInputType(InputType.TYPE_NULL);
        }



        //自动弹出软键盘
        et_content.requestFocus();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
                           public void run()
                           {
                               InputMethodManager inputManager =
                                       (InputMethodManager)et_content.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

                               inputManager.showSoftInput(et_content, 0);
                           }
                       },
                500);

        TextView tv_save = (TextView) this.findViewById(R.id.tv_save);
        tv_save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String newcontent = et_content.getText().toString().trim();
                if (newcontent.equals("") || newcontent.equals("0")) {
                    return;
                }
                LocalUserInfo.getInstance(ChangeUserParaActivity.this).setUserInfo(content, newcontent);
                finish();
            }
        });
    }


    public void back(View view) {
        finish();
    }

    /**
     * 隐藏输入键盘
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if(null != this.getCurrentFocus()){
                /**
                 * 点击空白位置 隐藏软键盘
                 */
                InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
            }
        }
        return super.onTouchEvent(event);
    }
}
