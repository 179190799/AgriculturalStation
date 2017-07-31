package com.rifeng.agriculturalstation.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.request.BaseRequest;
import com.rifeng.agriculturalstation.BaseActivity;
import com.rifeng.agriculturalstation.R;
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
 * 登录
 * <p>
 * Created by chw on 2016/10/14.
 */
public class LoginActivity extends BaseActivity {

    @BindView(R.id.login_username)
    EditText loginUsername;
    @BindView(R.id.login_password)
    EditText loginPassword;
    @BindView(R.id.login_login_btn)
    Button loginLoginBtn;
    @BindView(R.id.login_forgot_password)
    TextView loginForgotPassword;
    @BindView(R.id.login_register_account)
    TextView loginRegisterAccount;

    private CustomProgressDialog dialog;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initData() {
        // 初始化加载进度条
        dialog = new CustomProgressDialog(this, "正在登录...");
    }

    private boolean checkParams() {
        if (TextUtils.isEmpty(loginUsername.getText().toString().trim()) || TextUtils.isEmpty(loginPassword.getText().toString().trim())) {
            ToastUtil.showShort(this, "请输入完整信息");
            return false;
        }
        return true;
    }

    private void goLogin() {
        dialog.show();
        // 拼接参数
        OkGo.post(Urls.URL_USER_LOGIN)
            .tag(this)
            .params("username", loginUsername.getText().toString().trim())
            .params("password", loginPassword.getText().toString().trim())
            .execute(new JsonCallback<ServerResult>() {
                @Override
                public void onSuccess(ServerResult serverResult, Call call, Response response) {
                    if(dialog != null && dialog.isShowing()){
                        dialog.dismiss();
                    }
                    ToastUtil.showShort(mContext, serverResult.msg);
                    if (serverResult.code == 200) {
                        // 存储用户uid
                        SharedPreferencesUtil.put(LoginActivity.this, Consts.USER_UID, serverResult.data.uid);
                        // 存储用户名
                        SharedPreferencesUtil.put(LoginActivity.this, Consts.USER_USERNAME, serverResult.data.username);
                        // 存储手机号
                        SharedPreferencesUtil.put(LoginActivity.this, Consts.USER_PHONE, serverResult.data.phone);
                        // 存储用户头像
                        SharedPreferencesUtil.put(LoginActivity.this, Consts.USER_AVATAR, serverResult.data.avatar);
                        // 存储用户的注册类型，农场主 1/农机手 2
                        SharedPreferencesUtil.put(LoginActivity.this, Consts.USER_REGTYPE, serverResult.data.regtype);
                        finish();
                    }
                }

                @Override
                public void onError(Call call, Response response, Exception e) {
                    super.onError(call, response, e);
                    ToastUtil.showShort(mContext, "网络错误");
                    if(dialog != null && dialog.isShowing()){
                        dialog.dismiss();
                    }
                }
            });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.cancel();
            }
            dialog = null;
        }
    }

    @OnClick({R.id.login_login_btn, R.id.login_forgot_password, R.id.login_register_account})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_login_btn: // 登录
                if (checkParams()) {
                    goLogin();
                }
                break;

            case R.id.login_forgot_password: // 忘记密码
                startActivity(ForgotPasswordActivity.class);
                break;

            case R.id.login_register_account: // 注册账户
                startActivity(RegisterActivity.class);
                break;
        }
    }
}
