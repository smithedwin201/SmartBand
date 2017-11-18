package com.test.smartband.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.test.smartband.R;
import com.test.smartband.activity.MatchAirConditionActivity;
import com.test.smartband.activity.PedometerActivity;
import com.test.smartband.activity.SuggestionsActivity;

/**
 * Created by chuangfeng on 2017/2/23.
 */

public class MenuPopWindow extends PopupWindow {

    private final View conentView;

    public MenuPopWindow(final Activity context) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.popupwindow_add, null);

        // 设置SelectPicPopupWindow的View
        this.setContentView(conentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);

        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimationPreview);

        RelativeLayout matchingRelativeLayout = (RelativeLayout) conentView.findViewById(R.id.re_match_aircondition);
        RelativeLayout setPedometerRelativeLayout = (RelativeLayout) conentView.findViewById(R.id.re_setPedometer);
        RelativeLayout helpRelativeLayout = (RelativeLayout) conentView.findViewById(R.id.re_help);

        matchingRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context,MatchAirConditionActivity.class));
                MenuPopWindow.this.dismiss();
            }
        });

        setPedometerRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context,PedometerActivity.class));
                MenuPopWindow.this.dismiss();
            }
        });

        helpRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context,SuggestionsActivity.class));
                MenuPopWindow.this.dismiss();
            }
        });
    }

    /**
     * 显示popupWindow
     */
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            this.showAsDropDown(parent, 0, 0);
        } else {
            this.dismiss();
        }
    }
}
