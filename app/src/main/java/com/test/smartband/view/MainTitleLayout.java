package com.test.smartband.view;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.test.smartband.R;
import com.test.smartband.activity.RunActivity;

public class MainTitleLayout extends LinearLayout{


    private ImageView mRunImageView;
    private ImageView mItemImageView;
    private TextView mTitleTextView;

    public MainTitleLayout(Context context) {
        super(context);
        initView(context);
    }

    public MainTitleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MainTitleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public MainTitleLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private void initView(final Context context ) {

        LayoutInflater.from(context).inflate(R.layout.main_title_bar, this);
        mRunImageView = (ImageView) findViewById(R.id.title_bar_run);
        mItemImageView = (ImageView) findViewById(R.id.title_bar_item);
        mTitleTextView = (TextView) findViewById(R.id.title_bar_title);

        mRunImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RunActivity.class);
                context.startActivity(intent);
            }
        });

        mItemImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuPopWindow popWindow = new MenuPopWindow((Activity) context);
                popWindow.showPopupWindow(mItemImageView);
            }
        });
    }

    public void setRunImageViewVisible(boolean visible) {
        if (visible) {
            mRunImageView.setVisibility(VISIBLE);
        }else {
            mRunImageView.setVisibility(GONE);
        }
    }

    public void setMenuImageViewVisible(boolean visible) {
        if (visible) {
            mItemImageView.setVisibility(VISIBLE);
        }else {
            mItemImageView.setVisibility(GONE);
        }
    }

    public void setTitle(String title) {
        mTitleTextView.setText(title);
    }
}
