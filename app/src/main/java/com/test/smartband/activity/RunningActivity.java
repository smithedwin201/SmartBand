package com.test.smartband.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.test.smartband.R;
import com.test.smartband.tools.CacheUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class RunningActivity extends AppCompatActivity {

    private ImageView runnigBeginImageView;
    private ImageView yellowCircleImage;
    private AnimationSet mSet;
    private ImageView runningPauseImageView;
    private ImageView runningStopImageView;
    private AnimationSet mLeftAnimationSet;
    private AnimationSet mRightAnimationSet;
    private TranslateAnimation mTranslateToRight;
    private TranslateAnimation mTranslateToLeft;
    private LinearLayout pauseLinearLayout;
    private ImageView soundOnImageView;
    private ImageView soundOffImageView;
    private Context mContext;
    private Chronometer mChronometer;
    private long rangeTime = 0;
    private LinearLayout runningRecordLinearLayout;
    private AlphaAnimation mAlphaAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running);
        mContext = this;

        //初始化
        initView();
        //添加监听器
        addListener();
        //设置动画效果
        addAnimation();

        yellowCircleImage.startAnimation(mSet);
        if (CacheUtils.getBoolean(mContext, CacheUtils.IS_OPEN_RUN_SOUND, true)) {
            soundOnImageView.setVisibility(View.VISIBLE);
            soundOffImageView.setVisibility(View.GONE);
        } else {
            soundOnImageView.setVisibility(View.GONE);
            soundOffImageView.setVisibility(View.VISIBLE);
        }

        //设置基础计时点，当前时间点，当计时的时候会用当前实时减去基础计时点，即一开始为00:00:00
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();
    }

    private void addListener() {
        RunningListener listener = new RunningListener();
        runningPauseImageView.setOnClickListener(listener);
        runnigBeginImageView.setOnClickListener(listener);
        runningStopImageView.setOnClickListener(listener);
        soundOffImageView.setOnClickListener(listener);
        soundOnImageView.setOnClickListener(listener);

        mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                //使计时器从00:00:00开始计时
                long time = SystemClock.elapsedRealtime() - chronometer.getBase();
                Date date = new Date(time);
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                mChronometer.setText(sdf.format(date));
            }
        });
    }

    private void addAnimation() {

        /**
         * 闪烁动画
         */
        mAlphaAnimation = new AlphaAnimation(0.2f, 5f);
        mAlphaAnimation.setDuration(1000);
        mAlphaAnimation.setRepeatCount(Animation.INFINITE);//无限大，即设置动画无限循环
        mAlphaAnimation.setRepeatMode(Animation.REVERSE);//重复


        /**
         * 从10倍大缩小到本身大小的动画
         */
        mSet = new AnimationSet(false);
        //缩放动画
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                10, 1,  // x轴从1倍放大到8倍
                10, 1,  // y轴从1倍放大到8倍
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
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //当动画结束的时候调用该方法
                runningPauseImageView.setVisibility(View.VISIBLE);
                yellowCircleImage.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

    }

    private void initView() {
        runnigBeginImageView = (ImageView) findViewById(R.id.iv_running_begin);
        runningPauseImageView = (ImageView) findViewById(R.id.iv_running_pause);
        runningStopImageView = (ImageView) findViewById(R.id.iv_running_stop);
        yellowCircleImage = (ImageView) findViewById(R.id.iv_circle_yellow);
        pauseLinearLayout = (LinearLayout) findViewById(R.id.linear_pause);
        soundOnImageView = (ImageView) findViewById(R.id.iv_running_sound_on);
        soundOffImageView = (ImageView) findViewById(R.id.iv_running_sound_off);
        mChronometer = (Chronometer) findViewById(R.id.chronometer_running);
        runningRecordLinearLayout = (LinearLayout) findViewById(R.id.ll_running_record);
    }

    private class RunningListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_running_begin:
                    pauseLinearLayout.setVisibility(View.GONE);
                    runningPauseImageView.setVisibility(View.VISIBLE);
                    /**
                     * 用当前实时时间减去记录的时间作为计时基础点，即让显示为刚才记录的时间，
                     * 保证时间显示从刚才暂停的地方继续下去。
                     *
                     * 举个例子@创锋：
                     * 当前时间为10点0分0秒（10:00:00），用setBase()设置为基础计时点时，
                     * 那么计时过程中总是用实时时间减去基础计时点（10点），若过了10秒钟，即实时时间为10点0分10秒
                     * 时间差显示在chronomer控件上为00:00:10，当按下暂停时，实时时间仍会继续增加，
                     * 若一直暂停到10点0分40秒，那么如果此时按下继续键时，chronomer控件显示是当前实时时间减去
                     * 基础时间点的时间差（即10:00:40 - 10:00:00 = 40），时间差为40秒，会显示00:00:40，跟刚才
                     * 暂停的时间00:00:10显然不同，并不是从00:00:10继续开始计时。因此需要重新设置基础时间点，
                     * 将实时时间减去暂停时显示的时间差，即10:00:40 - 00:00:10 = 10:00:30，那么从10点0分30秒
                     * 作为基础时间点计时，到现在10:00:40刚好过了10秒，因此chronomer控件显示时间差为10秒，并从
                     * 此继续计时下去，达到连续计时的效果。
                     */
                    mChronometer.setBase(SystemClock.elapsedRealtime() - rangeTime);
                    mChronometer.start();
                    runningRecordLinearLayout.clearAnimation();
                    break;
                case R.id.iv_running_pause:
                    runningPauseImageView.setVisibility(View.GONE);
                    pauseLinearLayout.setVisibility(View.VISIBLE);
                    //保存时间差，即计时器记录的时间
                    rangeTime = SystemClock.elapsedRealtime() - mChronometer.getBase();
                    mChronometer.stop();
                    //闪烁动画开始
                    runningRecordLinearLayout.startAnimation(mAlphaAnimation);
                    break;

                case R.id.iv_running_stop:
                    Intent intent = new Intent(mContext, RunActivity.class);
                    startActivity(intent);
                    finish();
                    break;

                case R.id.iv_running_sound_off:
                    soundOffImageView.setVisibility(View.GONE);
                    soundOnImageView.setVisibility(View.VISIBLE);
                    CacheUtils.putBoolean(mContext, CacheUtils.IS_OPEN_RUN_SOUND, true);
                    break;
                case R.id.iv_running_sound_on:
                    soundOffImageView.setVisibility(View.VISIBLE);
                    soundOnImageView.setVisibility(View.GONE);
                    CacheUtils.putBoolean(mContext, CacheUtils.IS_OPEN_RUN_SOUND, false);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onBackPressed(){
        //设置返回按键失效
    }
}
