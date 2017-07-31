package com.rifeng.agriculturalstation.activity;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.lzy.okgo.OkGo;
import com.rifeng.agriculturalstation.BaseActivity;
import com.rifeng.agriculturalstation.R;
import com.rifeng.agriculturalstation.alipay.PayResult;
import com.rifeng.agriculturalstation.bean.ServerResultModel;
import com.rifeng.agriculturalstation.callback.JsonCallback;
import com.rifeng.agriculturalstation.utils.Consts;
import com.rifeng.agriculturalstation.utils.SharedPreferencesUtil;
import com.rifeng.agriculturalstation.utils.ToastUtil;
import com.rifeng.agriculturalstation.utils.Urls;

import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 充值
 * <p>
 * Created by chw on 2016/10/24.
 */
public class RechargeActivity extends BaseActivity {

    @BindView(R.id.id_title_left)
    TextView idTitleLeft;
    @BindView(R.id.id_title_middle)
    TextView idTitleMiddle;
    @BindView(R.id.recharge_amount_et)
    EditText rechargeAmountEt; // 充值金额
    @BindView(R.id.recharge_amount_delete)
    ImageView rechargeAmountDelete;
    @BindView(R.id.recharge_radio_alipay)
    RadioButton rechargeRadioAlipay; // 支付宝
    @BindView(R.id.recharge_radio_weixin)
    RadioButton rechargeRadioWeixin; // 微信
    @BindView(R.id.recharge_next_btn)
    Button rechargeNextBtn;

    private int payType = 1; // 支付类型：1 支付宝（默认） 2 微信
    public static final int ALI_PAY_FLAG = 0x111;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == ALI_PAY_FLAG){
                @SuppressWarnings("unchecked")
                PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                /**
                 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                 */
                String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                String resultStatus = payResult.getResultStatus();
                // 判断resultStatus 为9000则代表支付成功
                if (TextUtils.equals(resultStatus, "9000")) {
                    // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                    Toast.makeText(RechargeActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                } else {
                    // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                    Toast.makeText(RechargeActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    protected int getContentViewId() {
        return R.layout.activity_recharge;
    }

    @Override
    protected void initData() {
        idTitleMiddle.setText("充值");
    }

    /**
     * 充值
     */
    private void recharge() {
        String url = "";
        if(payType == 1){ // 支付宝
            url = "";
        }else if(payType == 2){ // 微信
            url = "";
        }

        // 拼接参数 Urls.URL_RECHARGE
        OkGo.post(url)
            .tag(this)
            .params("uid", (int) SharedPreferencesUtil.get(mContext, Consts.USER_UID, 0), true)
            .params("money", rechargeAmountEt.getText().toString().trim())
            .execute(new JsonCallback<ServerResultModel>() {

                @Override
                public void onSuccess(ServerResultModel serverResultModel, Call call, Response response) {
                    // 支付行为需要在独立的非ui线程中执行
                    final String orderInfo = "订单信息";
                    Runnable payRunnable = new Runnable() {
                        @Override
                        public void run() {
                            PayTask alipay = new PayTask(RechargeActivity.this);
                            Map<String, String> result = alipay.payV2(orderInfo, true);

                            Message msg = new Message();
                            msg.what = ALI_PAY_FLAG;
                            msg.obj = result;
                            mHandler.sendMessage(msg);
                        }
                    };
                    // 必须异步调用
                    Thread payThread = new Thread(payRunnable);
                    payThread.start();

//                    ToastUtil.showShort(mContext, serverResultModel.msg);
//                    if (serverResultModel.code == 200) {
//                        finish();
//                    }
                }

                @Override
                public void onError(Call call, Response response, Exception e) {
                    super.onError(call, response, e);
//                    ToastUtil.showShort(mContext, "充值失败");
                }
            });
    }

    @OnClick({R.id.id_title_left, R.id.recharge_amount_delete, R.id.recharge_next_btn, R.id.recharge_radio_alipay, R.id.recharge_radio_weixin})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_title_left: // 返回
                finish();
                break;

            case R.id.recharge_radio_alipay: // 支付宝
                payType = 1;
                break;

            case R.id.recharge_radio_weixin: // 微信
                payType = 2;
                break;

            case R.id.recharge_next_btn: // 下一步
                if (!TextUtils.isEmpty(rechargeAmountEt.getText().toString().trim())) {
//                    recharge();
                } else {
                    ToastUtil.showShort(this, "请输入充值金额");
                }
                break;

            case R.id.recharge_amount_delete: // 删除
                rechargeAmountEt.setText(null);
                break;
        }
    }
}
