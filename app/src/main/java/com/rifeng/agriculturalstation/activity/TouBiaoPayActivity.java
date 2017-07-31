package com.rifeng.agriculturalstation.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lzy.okgo.OkGo;
import com.rifeng.agriculturalstation.BaseActivity;
import com.rifeng.agriculturalstation.R;
import com.rifeng.agriculturalstation.bean.ServerResult;
import com.rifeng.agriculturalstation.callback.JsonCallback;
import com.rifeng.agriculturalstation.utils.AsyncHttpUtil;
import com.rifeng.agriculturalstation.utils.Consts;
import com.rifeng.agriculturalstation.utils.CustomProgressDialog;
import com.rifeng.agriculturalstation.utils.SharedPreferencesUtil;
import com.rifeng.agriculturalstation.utils.ToastUtil;
import com.rifeng.agriculturalstation.utils.Urls;


import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 投标支付
 * <p>
 * Created by chw on 2016/11/28.
 */
public class TouBiaoPayActivity extends BaseActivity {

    @BindView(R.id.id_title_left)
    TextView idTitleLeft;
    @BindView(R.id.id_title_middle)
    TextView idTitleMiddle;
    @BindView(R.id.toubiao_balance)
    TextView balance; // 余额
    @BindView(R.id.toubiao_cost)
    TextView payCost; // 支付金额
    @BindView(R.id.toubiao_radio_alipay)
    RadioButton radioAlipay; // 支付宝
    @BindView(R.id.toubiao_radio_weixin)
    RadioButton radioWeixin; // 微信
    @BindView(R.id.toubiao_radio_balance)
    RadioButton radioBalance; // 余额
    @BindView(R.id.toubiao_radio_group)
    RadioGroup toubiaoRadioGroup;
    @BindView(R.id.toubiao_next_btn)
    Button nextBtn; // 下一步

    private int payType = 1; // 选择支付的方式，默认=1，1支付宝 2微信 3余额
    private double accountBalance; // 余额
    private int taskid;
//    private double taskPrice; // 任务价格
    private CustomProgressDialog dialog;

    //1 表示未参与 0表示已参与
//    private boolean isPay = false;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_toubiaopay;
    }

    @Override
    protected void initData() {
        dialog = new CustomProgressDialog(this, "请稍候...");
        idTitleMiddle.setText("支付");
//        taskid = this.getIntent().getExtras().getInt("taskid");
//        taskPrice = Double.valueOf(this.getIntent().getExtras().getString("payCost"));
        Bundle bundle = this.getIntent().getExtras();
        taskid = bundle.getInt("taskid");
//        taskPrice = Double.parseDouble(bundle.getString("payCost"));
//        payCost.setText(Html.fromHtml("本次需支付金额为：<font color='#FF9F3F'>" + taskPrice + "</font>元"));
        getBalance();

        Log.e(TAG, "uid: " + (int) SharedPreferencesUtil.get(mContext, Consts.USER_UID, 0));
        Log.e(TAG, "taskid: " + taskid);

        toubiaoRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                payType = group.indexOfChild(group.findViewById(checkedId)) + 1;
//                ToastUtil.showShort(mContext, "选择了 = " + payType);
            }
        });
    }

    /**
     * 获取账户余额
     */

    private void getBalance() {
        RequestParams params = new RequestParams();
        params.put("uid", SharedPreferencesUtil.get(mContext, Consts.USER_UID, 0));

        AsyncHttpUtil.post(Urls.URL_ACCOUNT_INFO, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    accountBalance = response.getDouble("balance");
                    balance.setText(Html.fromHtml("您的账户余额：<font color='#FF9F3F'>" + accountBalance + "</font> 元"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                ToastUtil.showShort(mContext, "获取失败");
            }
        });
    }
//    private void obtainData() {
//        OkGo.post(Urls.URL_ACCOUNT_BALANCE)
//                .tag(this)
//                .params("uid", (int) SharedPreferencesUtil.get(mContext, Consts.USER_UID, 0))
//                .execute(new JsonCallback<ServerResult>() {
//                    @Override
//                    public void onSuccess(ServerResult serverResult, Call call, Response response) {
//                        accountBalance = Double.valueOf(serverResult.msg);
//                        balance.setText(Html.fromHtml("您的账户余额：<font color='#FF9F3F'>" + accountBalance + "</font>元"));
//                    }
//
//                    @Override
//                    public void onError(Call call, Response response, Exception e) {
//                        super.onError(call, response, e);
//                        ToastUtil.showShort(mContext, "获取失败");
//                    }
//                });
//    }

    private boolean CheckParams() {
        double taskPrice = 2000;
        if (accountBalance < taskPrice) {
            ToastUtil.showShort(mContext, "您的余额不足");
            return false;
        }
        return true;
    }

    /**
     * 余额支付项目款
     */
    private void payTaskCost() {
        OkGo.post(Urls.URL_BALANCE_TBPAY)
                .tag(this)
                .params("uid", (int) SharedPreferencesUtil.get(mContext, Consts.USER_UID, 0))
                .params("taskid", taskid)
                .params("paytype", payType)
                .execute(new JsonCallback<ServerResult>() {
                    @Override
                    public void onSuccess(ServerResult serverResult, Call call, Response response) {
                        ToastUtil.showShort(TouBiaoPayActivity.this, serverResult.msg);
                        if (serverResult.code == 200) {
                            ToastUtil.showShort(mContext, serverResult.msg);
                            TouBiaoPayActivity.this.finish();
                        } else if (serverResult.code == 101) {
                            ToastUtil.showShort(mContext, serverResult.msg);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.showShort(mContext, "支付失败");
                    }
                });
    }

    @OnClick({R.id.id_title_left, R.id.toubiao_next_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_title_left: // 返回
                finish();
                break;

            case R.id.toubiao_next_btn: // 下一步，调起支付
                if (payType == 3) { // 余额支付，判断余额是否充足
                    if (CheckParams()) {
                        AlertDialog alertDialog = new AlertDialog.Builder(this)
                                .setMessage("确定进行支付吗？")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        payTaskCost();
                                    }
                                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).create();
                        alertDialog.show();
                    }
                } else if (payType == 1) { // 支付宝支付
                    ToastUtil.showShort(this, "抱歉，尚未开通该功能。");
//                    payTaskCost();
                } else if (payType == 2) { // 微信支付
                    ToastUtil.showShort(this, "抱歉，尚未开通该功能。");
                }
                break;
        }
    }
}
