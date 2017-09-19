package com.rifeng.agriculturalstation.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
import com.rifeng.agriculturalstation.bean.EeventBusBean;
import com.rifeng.agriculturalstation.bean.ServerResult;
import com.rifeng.agriculturalstation.callback.JsonCallback;
import com.rifeng.agriculturalstation.utils.AsyncHttpUtil;
import com.rifeng.agriculturalstation.utils.CommonAdapter;
import com.rifeng.agriculturalstation.utils.Consts;
import com.rifeng.agriculturalstation.utils.CustomProgressDialog;
import com.rifeng.agriculturalstation.utils.SharedPreferencesUtil;
import com.rifeng.agriculturalstation.utils.ToastUtil;
import com.rifeng.agriculturalstation.utils.Urls;

import org.apache.http.Header;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 支付进度款的支付方式
 * Created by Administrator on 2017/9/6.
 */

public class PayStagesPayWayActivity extends BaseActivity {
    @BindView(R.id.id_title_left)
    TextView idTitleLeft;
    @BindView(R.id.id_title_middle)
    TextView idTitleMiddle;
    @BindView(R.id.id_title_right)
    TextView idTitleRight;
    @BindView(R.id.pay_balance_tv)
    TextView payBalanceTv;//账户余额
    @BindView(R.id.pay_task_money_tv)
    TextView payTaskMoneyTv;//需要支付的金额
    @BindView(R.id.pay_radio_alipay)
    RadioButton payRadioAlipay;//支付宝
    @BindView(R.id.pay_radio_weixin)
    RadioButton payRadioWeixin;//微信
    @BindView(R.id.pay_radio_balance)
    RadioButton payRadioBalance;//余额支付
    @BindView(R.id.pay_complete_bt)
    Button payCompleteBt;//完成

    private String money;//需要支付的金额
    private int id;//款项分期的期数id
    private int paytype = 3;//支付方式，默认账户余额支付.1 支付宝。2 微信。
    private int taskid;//任务id
    private double balancemoney = 0.0;
    private CustomProgressDialog mDialog;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_pay_project_bond;
    }

    @Override
    protected void initData() {
        mDialog = new CustomProgressDialog(mContext, "正在加载中...");
        idTitleRight.setVisibility(View.GONE);
        idTitleMiddle.setText("请选择支付方式！");

        Bundle bundle = this.getIntent().getExtras();
        id = bundle.getInt("id");
        taskid = bundle.getInt("taskid");
        money = bundle.getString("money");
        payTaskMoneyTv.setText(""+money);
        getAccountBalance(true);
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




    /**
     * 支付进度款项
     */
    private void payStages(int paytype) {
        // 拼接参数
        OkGo.post(Urls.URL_TASK_BALANCE_PAY_STAGES)
                .tag(this)
                .params("stagesid", id)
                .params("uid", (int) SharedPreferencesUtil.get(mContext, Consts.USER_UID, 0))
                .params("taskid", taskid)
                .params("paytype", paytype)
                .execute(new JsonCallback<ServerResult>() {
                    @Override
                    public void onSuccess(ServerResult serverResult, Call call, Response response) {
                        if(serverResult != null){
                            ToastUtil.showShort(mContext, serverResult.msg);
                            if(serverResult.code == 200){ // 支付成功
                                finish();
                                EventBus.getDefault().post(new EeventBusBean("支付成功"));
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.showShort(mContext, "网络错误");
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
                    payStages(paytype);
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
     * 获取账户余额
     */
    private void getAccountBalance(boolean isShow) {
        if (isShow) {
            mDialog.show();
        }
        RequestParams params = new RequestParams();
        params.put("uid", SharedPreferencesUtil.get(mContext, Consts.USER_UID, 0));

        AsyncHttpUtil.post(Urls.URL_ACCOUNT_INFO, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                mDialog.dismiss();
                try {
                    if (!isFinishing()) {
                        balancemoney = response.getDouble("balance");
                        payBalanceTv.setText(balancemoney + "");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                mDialog.dismiss();
                ToastUtil.showShort(mContext, "获取失败");
            }
        });
    }
}
