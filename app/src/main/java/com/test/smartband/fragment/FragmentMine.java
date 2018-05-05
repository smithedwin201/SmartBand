package com.test.smartband.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.test.smartband.R;
import com.test.smartband.activity.AccountBindingActivity;
import com.test.smartband.activity.NewDeviceActivity;
import com.test.smartband.activity.ResetPasswordActivity;
import com.test.smartband.activity.SettingActivity;
import com.test.smartband.activity.StepTargetNumberActivity;
import com.test.smartband.activity.UsersActivity;
import com.test.smartband.activity.WeightTargetActivity;
import com.test.smartband.other.LocalUserInfo;
import com.test.smartband.tools.CacheUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;


public class FragmentMine extends Fragment {

    private View mView;
    private RelativeLayout mAvatarRelativeLayout;
    private LinearLayout mDeviceLinearLayout;
    private RelativeLayout mStepTargetRelativeLayout;
    private RelativeLayout mWeightTargetRelativeLayout;
    private RelativeLayout mChangePwdRelativeLayout;
    private RelativeLayout mBingingAccountRelativeLayout;
    private RelativeLayout mSettingRelativeLayout;
    private TextView targetStepTextView;
    private ImageView avatarImageView;
    private TextView weightTargetTextView;

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_mine, container, false);
        initView();
        Log.e("FragmentMine", "onCreateView");

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("FragmentMine", "onResume");

        //更新设置的目标步数
        String step = CacheUtils.getString(getContext(), CacheUtils.STEP_TARGET_NUMBER, 8000 + "");
        targetStepTextView.setText(step + " 步");
        String weight = CacheUtils.getString(getContext(), CacheUtils.WEIGHT_TARGET, 60 + "");
        weightTargetTextView.setText(weight + " Kg");
        //从本地数据库中取到保存头像的路径
        String avatarPath = LocalUserInfo.getInstance(getActivity())
                .getUserInfo("avatarPath");
        Bitmap photo = getUserAvatar(avatarPath);
        if (photo != null) {
            avatarImageView.setImageBitmap(photo);
        }
    }


    /**
     * 获得头像
     */
    private Bitmap getUserAvatar(String path) {
        if (!path.equals("")) {
            try {
                FileInputStream fis = new FileInputStream(path);
                Bitmap photo = BitmapFactory.decodeStream(fis);
                return photo;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void initView() {

        mAvatarRelativeLayout = (RelativeLayout) mView.findViewById(R.id.re_avatar);
        mDeviceLinearLayout = (LinearLayout) mView.findViewById(R.id.ll_device);
        mStepTargetRelativeLayout = (RelativeLayout) mView.findViewById(R.id.re_step_target);
        mWeightTargetRelativeLayout = (RelativeLayout) mView.findViewById(R.id.re_weight_target);
        mChangePwdRelativeLayout = (RelativeLayout) mView.findViewById(R.id.re_change_pwd);
        mBingingAccountRelativeLayout = (RelativeLayout) mView.findViewById(R.id.re_binging_account);
        mSettingRelativeLayout = (RelativeLayout) mView.findViewById(R.id.re_setting);

        targetStepTextView = (TextView) mView.findViewById(R.id.target_step_textView);
        weightTargetTextView = (TextView) mView.findViewById(R.id.target_weight_textView);
        // 头像
        avatarImageView = (ImageView) mView.findViewById(R.id.iv_avatar);

        mAvatarRelativeLayout.setOnClickListener(new FragmentMineListener());
        mDeviceLinearLayout.setOnClickListener(new FragmentMineListener());
        mStepTargetRelativeLayout.setOnClickListener(new FragmentMineListener());
        mWeightTargetRelativeLayout.setOnClickListener(new FragmentMineListener());
        mChangePwdRelativeLayout.setOnClickListener(new FragmentMineListener());
        mBingingAccountRelativeLayout.setOnClickListener(new FragmentMineListener());
        mSettingRelativeLayout.setOnClickListener(new FragmentMineListener());
    }


    class FragmentMineListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.re_avatar:
                    Intent intent = new Intent(getActivity(), UsersActivity.class);
                    startActivity(intent);
                    break;
                case R.id.ll_device:
                    startActivity(new Intent(getActivity(), NewDeviceActivity.class));
                    break;
                case R.id.re_step_target:
                    startActivity(new Intent(getActivity(), StepTargetNumberActivity.class));
                    break;
                case R.id.re_weight_target:
                    startActivity(new Intent(getActivity(), WeightTargetActivity.class));
                    break;
                case R.id.re_change_pwd:
                    Intent intentChangePwd = new Intent(getActivity(), ResetPasswordActivity.class);
                    intentChangePwd.putExtra("change_pwd", true);
                    startActivity(intentChangePwd);
                    break;
                case R.id.re_binging_account:
                    startActivity(new Intent(getActivity(), AccountBindingActivity.class));
                    break;
                case R.id.re_setting:
                    startActivity(new Intent(getActivity(), SettingActivity.class));
                    break;
            }
        }
    }
    @Override
    public void onDestroyView() {
        Log.e("FragmentMine", "onDestroyView");
        super.onDestroyView();
    }
}

