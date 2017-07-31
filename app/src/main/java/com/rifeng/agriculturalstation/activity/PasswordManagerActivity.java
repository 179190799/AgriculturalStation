package com.rifeng.agriculturalstation.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
 * 个人中心-密码管理
 * <p>
 * Created by chw on 2016/10/25.
 */
public class PasswordManagerActivity extends BaseActivity {

    @BindView(R.id.id_title_left)
    TextView idTitleLeft;
    @BindView(R.id.id_title_middle)
    TextView idTitleMiddle;
    @BindView(R.id.pm_old_et)
    EditText pmOldEt;
    @BindView(R.id.pm_old_delete)
    ImageView pmOldDelete;
    @BindView(R.id.pm_new_et)
    EditText pmNewEt;
    @BindView(R.id.pm_new_delete)
    ImageView pmNewDelete;
    @BindView(R.id.pm_confirmnew_et)
    EditText pmConfirmnewEt;
    @BindView(R.id.pm_confirmnew_delete)
    ImageView pmConfirmnewDelete;
    @BindView(R.id.pm_change_btn)
    Button pmChangeBtn;

    private String oldpwd;
    private String newpwd;
    private String confirmpwd;
    private CustomProgressDialog dialog;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_passwordmanager;
    }

    @Override
    protected void initData() {
        idTitleMiddle.setText("修改密码");
        dialog = new CustomProgressDialog(mContext, "");
    }

    private void submitData() {
        // 拼接参数
        OkGo.post(Urls.URL_MODIFY_PASSWORD)
            .tag(this)
            .params("uid", (int) SharedPreferencesUtil.get(mContext, Consts.USER_UID, 0), true)
            .params("oldpwd", oldpwd)
            .params("newpwd", newpwd)
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
                    if(serverResult.code == 200){
                        PasswordManagerActivity.this.finish();
                    }
                }

                @Override
                public void onError(Call call, Response response, Exception e) {
                    super.onError(call, response, e);
                    ToastUtil.showShort(mContext, "修改失败");
                    dialog.dismiss();
                }
            });
    }

    private boolean checkParams() {
        oldpwd = pmOldEt.getText().toString().trim();
        newpwd = pmNewEt.getText().toString().trim();
        confirmpwd = pmConfirmnewEt.getText().toString().trim();

        if (TextUtils.isEmpty(oldpwd) || TextUtils.isEmpty(newpwd) || TextUtils.isEmpty(confirmpwd)) {
            ToastUtil.showShort(mContext, "请先填写完整信息");
            return false;
        }
        if (!newpwd.equals(confirmpwd)) {
            ToastUtil.showShort(mContext, "两次密码输入不一致");
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            dialog = null;
        }
        super.onDestroy();
    }

    @OnClick({R.id.id_title_left, R.id.pm_old_delete, R.id.pm_new_delete, R.id.pm_confirmnew_delete, R.id.pm_change_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_title_left: // 返回
                finish();
                break;

            case R.id.pm_old_delete: // 原始密码-删除按钮
                pmOldEt.setText("");
                break;

            case R.id.pm_new_delete: // 新密码-删除按钮
                pmNewEt.setText("");
                break;

            case R.id.pm_confirmnew_delete: // 确认新密码-删除按钮
                pmConfirmnewEt.setText("");
                break;

            case R.id.pm_change_btn: // 确认修改
                if (checkParams()) {
                    submitData();
                }
                break;
        }
    }
}
