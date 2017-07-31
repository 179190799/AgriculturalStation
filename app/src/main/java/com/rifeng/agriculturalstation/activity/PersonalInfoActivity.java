package com.rifeng.agriculturalstation.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.rifeng.agriculturalstation.BaseActivity;
import com.rifeng.agriculturalstation.R;
import com.rifeng.agriculturalstation.utils.Consts;
import com.rifeng.agriculturalstation.utils.SharedPreferencesUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 个人中心-个人信息
 * <p>
 * Created by chw on 2016/10/21.
 */
public class PersonalInfoActivity extends BaseActivity {

    @BindView(R.id.id_title_left)
    TextView idTitleLeft;
    @BindView(R.id.id_title_middle)
    TextView idTitleMiddle;
    @BindView(R.id.id_title_right)
    TextView idTitleRight;
    @BindView(R.id.id_base_info)
    TextView  baseInfo; // 基础信息;
    @BindView(R.id.id_farme_info)
    TextView farmeInfo; // 农场信息/农机信息;

    private int regtype; // 用户的注册类型---1 农场信息  2 农机信息

    @Override
    protected int getContentViewId() {
        return R.layout.activity_personalinfo;
    }

    @Override
    protected void initData() {
        idTitleMiddle.setText("个人信息");
        regtype = (int) SharedPreferencesUtil.get(mContext, Consts.USER_REGTYPE, 0);
        if (regtype == 1) {
            farmeInfo.setText("农场信息");
        } else if (regtype == 2) {
            farmeInfo.setText("农机信息");
        }
    }

    @OnClick({R.id.id_title_left, R.id.id_base_info, R.id.id_farme_info})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_title_left: // 返回
                finish();
                break;

            case R.id.id_base_info: // 基础信息
                startActivity(BasePersonalInfoActivity.class);
                break;

            case R.id.id_farme_info: // 农场信息
                Bundle bundle = new Bundle();
                bundle.putString("username", (String) SharedPreferencesUtil.get(this, Consts.USER_USERNAME, ""));
                bundle.putInt("uid", (int) SharedPreferencesUtil.get(this, Consts.USER_UID, 0));
                if (regtype == 1) { // 农场信息
                    startActivity(FarmDetailsActivity.class, bundle);
                } else if (regtype == 2) { // 农机信息
                    startActivity(LocomotiveDetailsActivity.class, bundle);
                }
                break;
        }
    }
}
