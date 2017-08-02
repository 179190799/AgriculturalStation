package com.rifeng.agriculturalstation.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rifeng.agriculturalstation.BaseActivity;
import com.rifeng.agriculturalstation.R;
import com.rifeng.agriculturalstation.utils.AsyncHttpUtil;
import com.rifeng.agriculturalstation.utils.Consts;
import com.rifeng.agriculturalstation.utils.SharedPreferencesUtil;
import com.rifeng.agriculturalstation.utils.Urls;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 个人中心-账户信息
 * <p>
 * Created by chw on 2016/10/24.
 */
public class AccountInfoActivity extends BaseActivity {

    @BindView(R.id.id_title_left)
    TextView idTitleLeft;
    @BindView(R.id.id_title_middle)
    TextView idTitleMiddle;
    @BindView(R.id.account_balance_tv)
    TextView accountBalanceTv; // 账户余额
    @BindView(R.id.account_starts_ll)
    LinearLayout accountStartsLl;
    @BindView(R.id.account_integral_tv)
    TextView accountIntegralTv; // 个人积分
    @BindView(R.id.account_recharge_btn)
    Button accountRechargeBtn;
    @BindView(R.id.account_withdrawal_btn)
    Button accountWithdrawalBtn;

    private double tempBalance;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_accountinfo;
    }

    @Override
    protected void initData() {
        idTitleMiddle.setText("账户信息");
        // 获取数据
        obtainData();
    }

    private void obtainData() {
        RequestParams params = new RequestParams();
        params.put("uid", SharedPreferencesUtil.get(mContext, Consts.USER_UID, 0));

        AsyncHttpUtil.post(Urls.URL_ACCOUNT_INFO, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    tempBalance = response.getDouble("balance");
                    int integral = response.getInt("integral");

                    if (!AccountInfoActivity.this.isFinishing()) {
                        accountBalanceTv.setText("￥" + tempBalance);
                        accountIntegralTv.setText(integral + "");
                        setLevel(response.getInt("accountlevel"));

//                        Log.e(TAG, "tempBalance: " + "￥" + tempBalance);
//                        Log.e(TAG, "integral: " + integral + "");
//                        Log.e(TAG, "accountlevel: " + response.getInt("accountlevel"));
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

            }
        });
    }

    /**
     * 设置我的星级
     *
     * @param accountlevel
     */
    private void setLevel(int accountlevel) {
        if (accountlevel == 0) {
            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    new ViewGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            params.leftMargin = 10;
            imageView.setBackgroundResource(R.mipmap.ic_stars);
            accountStartsLl.addView(imageView, params);
        } else {

            // 星级数
            for (int i = 0; i < accountlevel; i++) {
                ImageView imageView = new ImageView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        new ViewGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                params.leftMargin = 10;
                imageView.setBackgroundResource(R.mipmap.ic_stars);
                accountStartsLl.addView(imageView, params);
            }
        }
    }

    @OnClick({R.id.id_title_left, R.id.account_recharge_btn, R.id.account_withdrawal_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_title_left: // 返回
                finish();
                break;

            case R.id.account_recharge_btn: // 充值
                startActivity(RechargeActivity.class);
                break;

            case R.id.account_withdrawal_btn: // 提现
                Bundle bundle = new Bundle();
                bundle.putString("balance", tempBalance + "");
                startActivity(WithDrawalActivity.class, bundle);
                break;
        }
    }
}
