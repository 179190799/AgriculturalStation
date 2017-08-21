package com.rifeng.agriculturalstation.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.request.BaseRequest;
import com.rifeng.agriculturalstation.BaseActivity;
import com.rifeng.agriculturalstation.R;
import com.rifeng.agriculturalstation.bean.ServerResult;
import com.rifeng.agriculturalstation.callback.JsonCallback;
import com.rifeng.agriculturalstation.utils.CountDownTimerUtils;
import com.rifeng.agriculturalstation.utils.CustomProgressDialog;
import com.rifeng.agriculturalstation.utils.ToastUtil;
import com.rifeng.agriculturalstation.utils.Urls;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 注册
 * <p>
 * Created by chw on 2016/10/14.
 */
public class RegisterActivity extends BaseActivity {

    @BindView(R.id.id_title_left)
    TextView idTitleLeft;
    @BindView(R.id.id_title_middle)
    TextView idTitleMiddle;
    @BindView(R.id.register_reg_type)
    TextView registerRegType;
    @BindView(R.id.register_user_type)
    TextView registerUserType;
    @BindView(R.id.register_username)
    EditText registerUsername;
    @BindView(R.id.register_password)
    EditText registerPassword;
    @BindView(R.id.register_confirm_password)
    EditText registerConfirmPassword;
    @BindView(R.id.register_phone_number)
    EditText registerPhoneNumber;
    @BindView(R.id.register_code)
    EditText registerCode;
    @BindView(R.id.register_cb)
    CheckBox registerCb;
    @BindView(R.id.register_agreement)
    TextView registerAgreement;
    @BindView(R.id.register_register_btn)
    Button registerRegisterBtn;
    @BindView(R.id.register_tip)
    TextView registerTip;
    @BindView(R.id.register_go_login)
    TextView registerGoLogin;
    @BindView(R.id.register_get_code)
    TextView registerGetCode;

    private CustomProgressDialog dialog;
    //    private TextView registerGetCode;
    private CountDownTimerUtils utils;

    private int i = 120;
    private int reg_type_flag = 0;
    private int user_type_flag = 0;

//    private Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            if (msg.what == -1) {
//                registerGetCode.setText("重新发送(" + i + ")");
//            } else if (msg.what == -2) {
//                registerGetCode.setText(R.string.getVerifyCode);
//                registerGetCode.setClickable(true);
//                i = 120;
//            }
//        }
//    };

    @Override
    protected int getContentViewId() {
        return R.layout.activity_register;
    }

    @Override
    protected void initData() {
//        registerGetCode = (TextView) findViewById(R.id.register_get_code);

        dialog = new CustomProgressDialog(this, "请稍候...");
        idTitleMiddle.setText(R.string.registerBtn);

        // 注册按钮默认不可点击
//        registerRegisterBtn.setClickable(false);
        registerRegisterBtn.setBackgroundResource(R.drawable.register_btn_selector);
    }

    /**
     * 用户类型选择
     * 个人、公司、企业
     */
    private void selectUserType() {
        // 得到构造器
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setSingleChoiceItems(R.array.select_user_type, user_type_flag, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // getResources().getStringArray(R.array.select_user_type)[which] 获取arrays.xml里的数组字段值
                registerUserType.setText(getResources().getStringArray(R.array.select_user_type)[which]);
                user_type_flag = which;
                dialog.dismiss();
            }
        });
        dialog.create().show();
    }

    /**
     * 注册类型选择
     * 农机主、农场主
     */
    private void selectRegType() {
        // 得到构造器
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setSingleChoiceItems(R.array.select_reg_type, reg_type_flag, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // getResources().getStringArray(R.array.select_reg_type)[which] 获取arrays.xml里的数组字段值
                registerRegType.setText(getResources().getStringArray(R.array.select_reg_type)[which]);
                reg_type_flag = which;
                dialog.dismiss();
            }
        });
        dialog.create().show();
    }

    /**
     * 发送注册信息到服务器
     */
    private void goRegister(String strType) {
        HttpParams params = new HttpParams();
        String url = "";
        if (strType.equals("code")) { // 获取验证码
            params.put("mobile", registerPhoneNumber.getText().toString().trim());
            url = Urls.URL_GET_AUTH_CODE;
        } else if (strType.equals("register")) { // 注册
            params.put("regtype", reg_type_flag + 1);
            params.put("usertype", user_type_flag + 1);
            params.put("username", registerUsername.getText().toString().trim());
            params.put("password", registerPassword.getText().toString().trim());
            params.put("phone", registerPhoneNumber.getText().toString().trim());
            params.put("mobile_code", registerCode.getText().toString().trim());

            url = Urls.URL_USER_REGISTER;
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
                        dialog.dismiss();
                        if (serverResult.code == 200) {
                            finish();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        dialog.dismiss();
                        ToastUtil.showShort(mContext, "注册失败");
                    }
                });
    }

    private boolean checkParams() {
        String username = registerUsername.getText().toString().trim();
        String password = registerPassword.getText().toString().trim();
        String confirm_password = registerConfirmPassword.getText().toString().trim();
        String phone = registerPhoneNumber.getText().toString().trim();
        String code = registerCode.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirm_password)
                || TextUtils.isEmpty(phone) || TextUtils.isEmpty(code)) {
            ToastUtil.showShort(this, "请填写完整信息");
            return false;
        }
        if (!password.equals(confirm_password)) {
            ToastUtil.showShort(this, "两次输入的密码不一致");
            return false;
        }
        return true;
    }

    @OnCheckedChanged(value = R.id.register_cb)
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) { // 选中
            registerRegisterBtn.setClickable(true);
            registerRegisterBtn.setBackgroundResource(R.drawable.mine_btn_selector);
        } else { // 未选中
            registerRegisterBtn.setClickable(false);
            registerRegisterBtn.setBackgroundResource(R.drawable.register_btn_selector);
        }
    }

    @OnClick({R.id.id_title_left, R.id.register_reg_type, R.id.register_user_type, R.id.register_get_code, R.id.register_agreement, R.id.register_register_btn, R.id.register_go_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_title_left: // 返回
                finish();
                break;

            case R.id.register_reg_type: // 注册类型
                selectRegType();
                break;

            case R.id.register_user_type: // 用户类型
                selectUserType();
                break;

            case R.id.register_agreement: // 服务协议
                break;

            case R.id.register_get_code: // 获取验证码

                if (!TextUtils.isEmpty(registerPhoneNumber.getText().toString().trim())) {
                    utils = new CountDownTimerUtils(registerGetCode, 60000, 1000);
                    utils.start();
                    sendCode();
                } else {
                    ToastUtil.showShort(mContext, "请输入手机号！");
                }

//                ToastUtil.showShort(this, "验证码已发送，请稍候!");
//                registerGetCode.setClickable(false);
//                registerGetCode.setText("重新发送(" + i + ")");
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        for (; i > 0; i--) {
//                            // TODO 更新UI，发送消息到Handler中进行处理
//                            mHandler.sendEmptyMessage(-1);
//                            if (i < 0) {
//                                break;
//                            }
//                            try {
//                                Thread.sleep(1000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        // TODO 更新UI，发送信息到Hanlder中进行处理
//                        mHandler.sendEmptyMessage(-2);
//                    }
//                }).start();
                break;

            case R.id.register_register_btn: // 注册
                if (checkParams()) {
                    goRegister("register");
                }
                break;

            case R.id.register_go_login: // 登录
                if (utils != null) {
                    utils.cancel();
                }
                finish();
                break;
        }
    }

    /**
     * 发送验证码
     */
    private void sendCode() {
        OkGo.post(Urls.URL_GET_AUTH_CODE)
                .tag(this)
                .params("mobile",registerPhoneNumber.getText().toString().trim())
                .execute(new JsonCallback<ServerResult>() {
                    @Override
                    public void onSuccess(ServerResult serverResult, Call call, Response response) {
                        ToastUtil.showShort(mContext,serverResult.msg);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.showShort(mContext,"发送失败!");
                    }
                });

    }
}
