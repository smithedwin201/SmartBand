package com.test.smartband.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.test.smartband.R;

/**
 * 跑步
 */
public class RunActivity extends AppCompatActivity {

    private static final int START_COUNTING = 1;
    private static final int COUNT_NUMBER = 3;

    private ImageView backImage;
    private ImageView settingImage;
    private TextView runRecordTextView;
    private ImageView runBeginImage;
    private TextView gpsOffTextView;
    private RunActivityListener listener;
    private LinearLayout runLinearLayout;
    private ImageView yellowCircleImageView;
    private LinearLayout timingLinearLayout;
    private TextView countTextView;
    private TimingHandler mHandler;
    private AnimationSet mSet;
    private TextView encourageTextView;
    private LinearLayout gpsLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);

        initView();
        addListener();
        addAnimation();

    }

    /**
     * 添加开始按钮点击动画
     */
    private void addAnimation() {
        mSet = new AnimationSet(false);
        //缩放动画
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                1, 8,  // x轴从1倍放大到8倍
                1, 8,  // y轴从1倍放大到8倍
                Animation.RELATIVE_TO_SELF, 0.5f,  // x轴从相对于自身的0.5倍位置开始缩放，即自身x轴的中间位置
                Animation.RELATIVE_TO_SELF, 0.5f   // y轴从相对于自身的0.5倍位置开始缩放，即自身y轴的中间位置
        );
        scaleAnimation.setDuration(700);//设置时长
        scaleAnimation.setFillAfter(true);//设置最终状态为填充效果
        //添加动画
        mSet.addAnimation(scaleAnimation);

        // 监听动画
        mSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                encourageTextView.setVisibility(View.GONE);
                gpsLinearLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //当动画结束的时候调用该方法
                runLinearLayout.setVisibility(View.GONE); //设置跑步界面隐藏
                timingLinearLayout.setVisibility(View.VISIBLE); //显示倒计时界面
                /**
                 * 从MessagePool 拿的,省去了创建对象申请内存的开销。
                 * 尽量使用 Message msg = handler.obtainMessage();的形式创建Message，
                 * 不要自己New Message，至于message产生之后你使用obtainMessage
                 * 或者是 sendMessage 效率影响并不大
                 */
                mHandler = new TimingHandler();
                Message msg = mHandler.obtainMessage();
                msg.what = START_COUNTING;
                msg.obj = COUNT_NUMBER;
                mHandler.sendMessage(msg);//发送消息进行倒计时
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * 设置监听器
     */
    private void addListener() {
        listener = new RunActivityListener();
        backImage.setOnClickListener(listener);
        settingImage.setOnClickListener(listener);
        runBeginImage.setOnClickListener(listener);
    }

    /**
     * 初始化
     */
    private void initView() {
        runLinearLayout = (LinearLayout) findViewById(R.id.linearLayout_run);
        backImage = (ImageView) findViewById(R.id.iv_back);
        settingImage = (ImageView) findViewById(R.id.iv_run_setting);
        runRecordTextView = (TextView) findViewById(R.id.tv_run_record);
        runBeginImage = (ImageView) findViewById(R.id.iv_run_begin);
        gpsOffTextView = (TextView) findViewById(R.id.tv_run_gps_off);
        yellowCircleImageView = (ImageView) findViewById(R.id.iv_circle_yellow);

        timingLinearLayout = (LinearLayout) findViewById(R.id.linearLayout_timing);
        countTextView = (TextView) findViewById(R.id.count_text);

        encourageTextView = (TextView) findViewById(R.id.encourage_text_run);
        gpsLinearLayout = (LinearLayout) findViewById(R.id.linearLayout_gps);
    }

    private class RunActivityListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.iv_back:
                    finish();
                    break;
                case R.id.iv_run_setting:
                    Intent intent = new Intent(RunActivity.this, RunSettingActivity.class);
                    startActivity(intent);

                    break;
                case R.id.iv_run_begin:
                    runBeginImage.setVisibility(View.GONE);
                    yellowCircleImageView.setVisibility(View.VISIBLE);
                    //展示动画
                    yellowCircleImageView.startAnimation(mSet);
                    break;
            }

        }
    }

    private class TimingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case START_COUNTING: //接收到开始倒计时的消息
                    int count = (int) msg.obj;
                    if (count == 0) {
                        countTextView.setTextSize(160);
                        countTextView.setText("GO!");
                        Intent i = new Intent(RunActivity.this, RunningActivity.class);
                        startActivity(i);
                        finish();
                        /**重写Activity的平移动画，该方法在startActivity或者finish 之后执行
                         * enterAnim：进入的动画
                         * exitAnim:退出动画
                         */
                        overridePendingTransition(R.anim.running_enter, R.anim.running_out);

                    } else {
                        countTextView.setText(count + "");
                    }

                    //当倒计时大于0时继续发送消息给自己进行倒计时
                    if (count > 0) {
                        Message message = obtainMessage();
                        message.what = START_COUNTING;
                        message.obj = count - 1;
                        sendMessageDelayed(message, 800);
                    }
                    break;
                default:
                    break;
            }
        }
    }


}
