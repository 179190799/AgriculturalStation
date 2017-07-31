package com.rifeng.agriculturalstation.activity;

import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.rifeng.agriculturalstation.BaseActivity;
import com.rifeng.agriculturalstation.R;
import com.rifeng.agriculturalstation.bean.ServerResult;
import com.rifeng.agriculturalstation.callback.JsonCallback;
import com.rifeng.agriculturalstation.utils.Consts;
import com.rifeng.agriculturalstation.utils.SharedPreferencesUtil;
import com.rifeng.agriculturalstation.utils.ToastUtil;
import com.rifeng.agriculturalstation.utils.Urls;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 提现
 * <p>
 * Created by chw on 2016/11/14.
 */
public class WithDrawalActivity extends BaseActivity {

    @BindView(R.id.id_title_left)
    TextView idTitleLeft;
    @BindView(R.id.id_title_middle)
    TextView idTitleMiddle;
    @BindView(R.id.withdrawal_balance)
    TextView balance; // 可用余额
    @BindView(R.id.withdrawal_price_tv)
    TextView withdrawalPriceTv;
    @BindView(R.id.withdrawal_price_et)
    EditText withdrawal_price; // 提现金额
    @BindView(R.id.withdrawal_price_unit)
    TextView withdrawalPriceUnit;
    @BindView(R.id.withdrawal_bank_card)
    TextView bank_card; // 银行卡
    @BindView(R.id.withdrawal_finish)
    Button okButton;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_withdrawal;
    }

    @Override
    protected void initData() {
        idTitleMiddle.setText("提现");
        balance.setText(Html.fromHtml("可提现金额：<font color='#FF9F3F'>" +
                this.getIntent().getExtras().getString("balance") + "</font> 元"));
    }

    private void submit() {
        // 拼接参数
        // TODO 地址还没有
        OkGo.post(Urls.URL_UPLOAD_CERTIFICATE)
            .tag(this)
            .params("uid", (int) SharedPreferencesUtil.get(mContext, Consts.USER_UID, 0), true)
            .params("price", withdrawal_price.getText().toString().trim())
            .execute(new JsonCallback<ServerResult>() {
                @Override
                public void onSuccess(ServerResult serverResult, Call call, Response response) {
                    ToastUtil.showShort(mContext, serverResult.msg);
                    if (serverResult.code == 200) {
                    }
                }

                @Override
                public void onError(Call call, Response response, Exception e) {
                    super.onError(call, response, e);
                    ToastUtil.showShort(mContext, "提现失败");
                }
            });
    }

    private boolean checkParams() {
        if (TextUtils.isEmpty(withdrawal_price.getText().toString().trim())) {
            ToastUtil.showShort(mContext, "请输入提现金额");
            return false;
        }
        return true;
    }

    @OnClick({R.id.id_title_left, R.id.withdrawal_bank_card, R.id.withdrawal_finish})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_title_left: // 返回
                finish();
                break;

            case R.id.withdrawal_bank_card: // 银行卡
                break;

            case R.id.withdrawal_finish: // 完成
                if (checkParams()) {
                    submit();
                }
                break;
        }
    }
}
