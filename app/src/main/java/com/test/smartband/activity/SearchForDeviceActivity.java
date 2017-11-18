package com.test.smartband.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.test.smartband.R;

/**
 * 搜索设备
 */
public class SearchForDeviceActivity extends AppCompatActivity {

    private RelativeLayout searchingRelativeLayout;
    private ImageView progressImageView;
    private TextView searchTextView;
    private TextView searchTipsTextView;
    private TextView searchControllerTextView;
    private TextView searchControllerTipsTextView;
    private AnimationDrawable progressAnimation;
    private AlphaAnimation mAlphaAnimation;
    private boolean handler;
    public static String SEARCHSELECT = "search_select";
    public static String BAND = "band";
    public static String CONTROLLER = "controller";
    private String intentStringExtra;
    private Context mContext;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_for_device);
        mContext = this;

        initView();
        Intent intent = getIntent();
        intentStringExtra = intent.getStringExtra(SEARCHSELECT);
        if (intentStringExtra.equals(BAND)) {
            searchTextView.setText("正在搜索您的手环");
            searchTipsTextView.setText("请将要绑定的手环放置在您的手机附近");
        } else if (intentStringExtra.equals(CONTROLLER)) {
            searchTextView.setText("正在搜索您的终端");
            searchTipsTextView.setText("请将您的手机靠近将要绑定的终端");
        }

        //闪烁动画
        mAlphaAnimation = new AlphaAnimation(0.1f, 1f);
        mAlphaAnimation.setDuration(800);
        mAlphaAnimation.setRepeatCount(Animation.INFINITE);//无限大，即设置动画无限循环
        mAlphaAnimation.setRepeatMode(Animation.REVERSE);
        searchingRelativeLayout.setAnimation(mAlphaAnimation);

        mAlphaAnimation.start();//动画开始
        progressAnimation.start();//动画开始

        //30秒钟后停止搜索动画
        handler = new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mAlphaAnimation.cancel();
                        progressAnimation.stop();

                        //如果搜索设备失败就跳转到失败界面
                        Intent intent = new Intent(mContext, SearchDeviceFailureActivity.class);
                        intent.putExtra(SEARCHSELECT, intentStringExtra);
                        startActivity(intent);
                        finish();


                    }
                }, 30*1000);
    }

    private void initView() {
        searchingRelativeLayout = (RelativeLayout) findViewById(R.id.re_searching);
        progressImageView = (ImageView) findViewById(R.id.refreshing_drawable_img);
        searchTextView = (TextView) findViewById(R.id.tv_search);
        searchTipsTextView = (TextView) findViewById(R.id.tv_search_tips);
        progressAnimation = (AnimationDrawable)progressImageView.getDrawable();
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(mContext, "正在搜索设备，请等待", Toast.LENGTH_SHORT).show();
    }
}
