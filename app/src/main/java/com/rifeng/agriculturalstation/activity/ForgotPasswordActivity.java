package com.rifeng.agriculturalstation.activity;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.request.BaseRequest;
import com.rifeng.agriculturalstation.BaseActivity;
import com.rifeng.agriculturalstation.R;
import com.rifeng.agriculturalstation.bean.ServerResult;
import com.rifeng.agriculturalstation.callback.JsonCallback;
import com.rifeng.agriculturalstation.utils.CustomProgressDialog;
import com.rifeng.agriculturalstation.utils.ToastUtil;
import com.rifeng.agriculturalstation.utils.Urls;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 忘记密码
 * <p/>
 * Created by chw on 2016/10/14.
 */
public class ForgotPasswordActivity extends BaseActivity {

    @BindView(R.id.id_title_left)
    TextView idTitleLeft;
    @BindView(R.id.id_title_middle)
    TextView idTitleMiddle;
    @BindView(R.id.forgot_phone_et)
    EditText forgotPhoneEt;
    @BindView(R.id.forgot_code_et)
    EditText forgotCodeEt;
    @BindView(R.id.forgot_newpassword_et)
    EditText forgotNewpasswordEt;
    @BindView(R.id.forgot_confirm_password_et)
    EditText forgotConfirmPasswordEt;
    @BindView(R.id.forgot_submit_btn)
    Button forgotSubmitBtn;

    private Button forgotGetCodeBtn;
    private int i = 120;
    private CustomProgressDialog dialog;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == -1) {
                forgotGetCodeBtn.setText("重新发送(" + i + ")");
            } else if (msg.what == -2) {
                forgotGetCodeBtn.setText(R.string.getVerifyCode);
                forgotGetCodeBtn.setClickable(true);
                i = 120;
            }
        }
    };

    @Override
    protected int getContentViewId() {
        return R.layout.activity_forgotpassword;
    }

    @Override
    protected void initData() {
        forgotGetCodeBtn = (Button) findViewById(R.id.forgot_get_code_btn);
        dialog = new CustomProgressDialog(this, "");
        idTitleMiddle.setText(R.string.retrievePassword);
    }

    private void confirmChange(String strType) {
        HttpParams params = new HttpParams();
        String url = "";
        if (strType.equals("code")) { // 获取验证码
            params.put("phone", forgotPhoneEt.getText().toString().trim());
            url = Urls.URL_GET_AUTH_CODE;
        } else if (strType.equals("change")) { // 找回密码
            params.put("phone", forgotPhoneEt.getText().toString().trim());
            params.put("newpassword", forgotNewpasswordEt.getText().toString().trim());
//            params.put("code", forgot_code_et.getText().toString().trim());
            url = Urls.URL_FIND_PASSWORD;
        }

        // 拼接参数
        OkGo.post(url)
            .tag(this)
            .params(params)
            .execute(new JsonCallback<ServerResult>() {
                @Override
                public void onBefore(BaseRequest request) {
                    super.onBefore(request);
                    dialog.show();
                }

                @Override
                public void onSuccess(ServerResult serverResult, Call call, Response response) {
                    ToastUtil.showShort(mContext, serverResult.msg);
                    if (serverResult.code == 200) {
                        dialog.dismiss();
                        finish();
                    }
                }

                @Override
                public void onError(Call call, Response response, Exception e) {
                    super.onError(call, response, e);
                    ToastUtil.showShort(mContext, "找回失败");
                    dialog.dismiss();
                }
            });
    }

    private boolean checkParams() {
        String phone = forgotPhoneEt.getText().toString().trim();
        String code = forgotCodeEt.getText().toString().trim();
        String new_password = forgotNewpasswordEt.getText().toString().trim();
        String confirm_password = forgotConfirmPasswordEt.getText().toString().trim();

        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(code)
                || TextUtils.isEmpty(new_password) || TextUtils.isEmpty(confirm_password)) {
            ToastUtil.showShort(this, "请填写完整信息");
            return false;
        }
        if (!new_password.equals(confirm_password)) {
            ToastUtil.showShort(this, "两次输入的密码不一致");
            return false;
        }
        return true;
    }

    @OnClick({R.id.id_title_left, R.id.forgot_get_code_btn, R.id.forgot_submit_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_title_left: // 返回
                finish();
                break;

            case R.id.forgot_get_code_btn: // 获取验证码
                // TODO 访问服务器获取验证码
//                confirmChange("code");

                ToastUtil.showShort(this, "验证码已发送，请稍候!");
                forgotGetCodeBtn.setClickable(false);
                forgotGetCodeBtn.setText("重新发送(" + i + ")");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (; i > 0; i--) {
                            // TODO 更新UI，发送消息到Handler中进行处理
                            mHandler.sendEmptyMessage(-1);
                            if (i < 0) {
                                break;
                            }
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        // TODO 更新UI，发送信息到Hanlder中进行处理
                        mHandler.sendEmptyMessage(-2);
                    }
                }).start();
                break;

            case R.id.forgot_submit_btn: // 确认修改
                if (checkParams()) {
                    confirmChange("change");
                }
                break;
        }
    }
}
