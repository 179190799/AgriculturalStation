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
import com.rifeng.agriculturalstation.bean.CodeBean;
import com.rifeng.agriculturalstation.bean.ServerModel;
import com.rifeng.agriculturalstation.bean.ServerResult;
import com.rifeng.agriculturalstation.callback.JsonCallback;
import com.rifeng.agriculturalstation.utils.Consts;
import com.rifeng.agriculturalstation.utils.CustomProgressDialog;
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
    @BindView(R.id.withdrawal_money)
    EditText withdrawalPrice; // 提现金额
    @BindView(R.id.withdrawal_bank_card)
    EditText withdrawalBankCard;//提现银行卡所属银行
    @BindView(R.id.withdrawal_account)
    EditText withdrawalAccount;//提现银行卡账号
    @BindView(R.id.withdrawal_userName)
    EditText withdrawalUserName;//提现的银行卡所有者姓名
    @BindView(R.id.withdrawal_finish)
    Button okButton;

    private CustomProgressDialog mDialog;



    @Override
    protected int getContentViewId() {
        return R.layout.activity_withdrawal;
    }

    @Override
    protected void initData() {
        mDialog = new CustomProgressDialog(mContext, "正在加载中...");
        idTitleMiddle.setText("提现");
        balance.setText(Html.fromHtml("可提现金额：<font color='#FF9F3F'>" +
                this.getIntent().getExtras().getString("balance") + "</font> 元"));
    }

    private void submit(boolean isShow) {
        if (isShow) {
            mDialog.show();
        }
        // 拼接参数
        OkGo.post(Urls.URL_WITH_DRAWAL)
                .tag(this)
                .params("uid", (int) SharedPreferencesUtil.get(mContext, Consts.USER_UID, 0), true)
                .params("money", withdrawalPrice.getText().toString().trim())
                .params("bankname", withdrawalBankCard.getText().toString().trim())
                .params("linkname",withdrawalUserName.getText().toString().trim())
                .params("payusername",withdrawalAccount.getText().toString().trim())
                .execute(new JsonCallback<CodeBean>() {
                    @Override
                    public void onSuccess(CodeBean codeBean, Call call, Response response) {
                        mDialog.dismiss();
                        ToastUtil.showShort(mContext, codeBean.getMsg());
                        if (codeBean.getCode()!=-1) {
                            finish();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        mDialog.dismiss();
                        ToastUtil.showShort(mContext, "提现失败");
                    }
                });
    }

    private boolean checkParams() {

        if (TextUtils.isEmpty( withdrawalPrice.getText().toString().trim())) {
            ToastUtil.showShort(mContext, "请输入提现金额");
            return false;
        }
        if (TextUtils.isEmpty( withdrawalBankCard.getText().toString().trim())) {
            ToastUtil.showShort(mContext, "请输入所属银行");
            return false;
        }
        if (TextUtils.isEmpty( withdrawalUserName.getText().toString().trim())) {
            ToastUtil.showShort(mContext, "请输入用户姓名");
            return false;
        }
        if (TextUtils.isEmpty( withdrawalAccount.getText().toString().trim())) {
            ToastUtil.showShort(mContext, "请输入银行卡号（支付宝账号）");
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

//            case R.id.withdrawal_bank_card: // 银行卡
//
//                break;

            case R.id.withdrawal_finish: // 完成
                if (checkParams()) {
                    submit(true);
                }
                break;
        }
    }
}
