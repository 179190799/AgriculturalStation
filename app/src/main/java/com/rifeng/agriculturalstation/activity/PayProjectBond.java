package com.rifeng.agriculturalstation.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lzy.okgo.OkGo;
import com.rifeng.agriculturalstation.BaseActivity;
import com.rifeng.agriculturalstation.R;
import com.rifeng.agriculturalstation.bean.EeventBusBean;
import com.rifeng.agriculturalstation.bean.ServerResult;
import com.rifeng.agriculturalstation.callback.JsonCallback;
import com.rifeng.agriculturalstation.utils.ActivityCollector;
import com.rifeng.agriculturalstation.utils.AsyncHttpUtil;
import com.rifeng.agriculturalstation.utils.Consts;
import com.rifeng.agriculturalstation.utils.LogUtil;
import com.rifeng.agriculturalstation.utils.SharedPreferencesUtil;
import com.rifeng.agriculturalstation.utils.ToastUtil;
import com.rifeng.agriculturalstation.utils.Urls;

import org.apache.http.Header;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/8/8.
 */

public class PayProjectBond extends BaseActivity {

    @BindView(R.id.id_title_left)
    TextView id_title_left_tv;
    @BindView(R.id.id_title_middle)
    TextView id_title_middle_tv;
    @BindView(R.id.pay_task_money_tv)
    TextView pay_task_money_tv; //需要支付金额
    @BindView(R.id.pay_radio_alipay)
    RadioButton pay_radio_alipay;//支付宝支付
    @BindView(R.id.pay_radio_weixin)
    RadioButton pay_radio_weixin;//微信支付
    @BindView(R.id.pay_radio_balance)
    RadioButton pay_radio_balance;//本地余额支付
    @BindView(R.id.pay_complete_bt)
    Button pay_complete_bt;//支付按钮
    @BindView(R.id.pay_balance_tv)
    TextView pay_balance_tv;//余额


    private int taskid;//该任务id
    private double taskmoney;//需要支付的金额

    private int paytype = 3; //支付方式
    private double balancemoney = 0.0; //余额
    private int startactivity;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_pay_project_bond;
    }

    @Override
    protected void initData() {
        id_title_middle_tv.setText("支付项目保证金");
        Bundle bundle = this.getIntent().getExtras();
        taskid = bundle.getInt("taskid");
        taskmoney = bundle.getDouble("taskmoney");
        startactivity = bundle.getInt("startactivity");
        pay_task_money_tv.setText("" + taskmoney);
        LogUtil.e(taskmoney + "taskmoney:");
        LogUtil.e(taskid + "taskid:");

        getAccountBalance();
    }


    /**
     * 获取账户余额
     */
    private void getAccountBalance() {
        RequestParams params = new RequestParams();
        params.put("uid", SharedPreferencesUtil.get(mContext, Consts.USER_UID, 0));

        AsyncHttpUtil.post(Urls.URL_ACCOUNT_INFO, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (!isFinishing()) {
                        balancemoney = response.getDouble("balance");
                        pay_balance_tv.setText(balancemoney + "");
                    }

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

    /**
     * 弹出支付提示框，
     */
    public void showDialog() {
        if (paytype == 3) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("是否确认支付");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    payTaskMoney();
//                    backActivity();
//                    defaultFinish();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        } else if (paytype == 1) {
            ToastUtil.showShort(this, "抱歉，尚未开通该功能。");
        } else if (paytype == 2) {
            ToastUtil.showShort(this, "抱歉，尚未开通该功能。");
        }
    }


    /**
     * 支付功能
     */
    public void payTaskMoney() {

        OkGo.post(Urls.URL_PAY_PROJECT_BOND)
                .tag(this)
                .params("uid", (int) SharedPreferencesUtil.get(mContext, Consts.USER_UID, 0), true)
                .params("taskid", taskid)
                .params("paytype", paytype)
                .execute(new JsonCallback<ServerResult>() {
                    @Override
                    public void onSuccess(ServerResult serverResult, Call call, Response response) {
                        ToastUtil.showShort(mContext, serverResult.msg);
                        if (serverResult.code == 200) {
//                            EventBus.getDefault().post(new EeventBusBean("ok"));
                            ToastUtil.showShort(mContext, serverResult.msg);
                            finish();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        try {
                            ToastUtil.showShort(mContext, "" + response.body().string());
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        ToastUtil.showShort(mContext, "支付失败");
                        finish();
                    }
                });
    }

    @OnClick({R.id.id_title_left, R.id.pay_radio_alipay, R.id.pay_radio_weixin, R.id.pay_radio_balance, R.id.pay_complete_bt})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_title_left://返回
                finish();
                break;
            case R.id.pay_radio_alipay://支付宝支付
                paytype = 1;
                break;
            case R.id.pay_radio_weixin://微信支付
                paytype = 2;
                break;
            case R.id.pay_radio_balance://本地余额支付
                paytype = 3;
                break;
            case R.id.pay_complete_bt://支付按钮
                showDialog();
                break;
        }
    }
}
