package com.rifeng.agriculturalstation.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.rifeng.agriculturalstation.BaseActivity;
import com.rifeng.agriculturalstation.R;
import com.rifeng.agriculturalstation.bean.FormBean;
import com.rifeng.agriculturalstation.bean.ServerResult;
import com.rifeng.agriculturalstation.callback.JsonCallback;
import com.rifeng.agriculturalstation.utils.Consts;
import com.rifeng.agriculturalstation.utils.CustomProgressDialog;
import com.rifeng.agriculturalstation.utils.LogUtil;
import com.rifeng.agriculturalstation.utils.SharedPreferencesUtil;
import com.rifeng.agriculturalstation.utils.ToastUtil;
import com.rifeng.agriculturalstation.utils.Urls;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/9/18.
 */

public class SeeTheFormActivity extends BaseActivity {
    @BindView(R.id.id_title_left)
    TextView idTitleLeft;
    @BindView(R.id.id_title_middle)
    TextView idTitleMiddle;
    @BindView(R.id.id_title_right)
    TextView idTitleRight;
    @BindView(R.id.total_money)
    TextView totalMoney;
    @BindView(R.id.total_number)
    TextView totalNumber;
    @BindView(R.id.ll_title)
    LinearLayout llTitle;
    @BindView(R.id.offermoney_1)
    EditText offermoney1;
    @BindView(R.id.offerday_1)
    EditText offerday1;
    @BindView(R.id.offermoney_2)
    EditText offermoney2;
    @BindView(R.id.offerday_2)
    EditText offerday2;
    @BindView(R.id.offermoney_3)
    EditText offermoney3;
    @BindView(R.id.offerday_3)
    EditText offerday3;
    @BindView(R.id.task_finish)
    TextView taskFinish;

    private CustomProgressDialog mDialog;
    private int taskid;
    private int uid;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_task_select;
    }

    @Override
    protected void initData() {
        mDialog = new CustomProgressDialog(mContext, "正在加载中...");
        llTitle.setVisibility(View.VISIBLE);
        idTitleMiddle.setText("查看报表");
        taskFinish.setText("选择此人");

        Bundle bundle = getIntent().getExtras();
        taskid = bundle.getInt("taskid");
        uid = bundle.getInt("uid");

        Log.e(TAG, "taskid: "+taskid);
        Log.e(TAG, "uid: "+uid);

        getFormData(true);

        offermoney1.setEnabled(false);
        offerday1.setEnabled(false);
        offerday2.setEnabled(false);
        offermoney2.setEnabled(false);
        offerday3.setEnabled(false);
        offermoney3.setEnabled(false);
    }

    /**
     * 获取表单数据
     * @param isShow
     */
    private void getFormData(boolean isShow) {
        if (isShow) {
            mDialog.show();
        }
        OkGo.post(Urls.URL_FORM_DATA)
                .tag(this)
                .params("uid", uid)
                .params("taskid", taskid)
                .execute(new JsonCallback<FormBean>() {
                    @Override
                    public void onSuccess(FormBean formBean, Call call, Response response) {
                        mDialog.dismiss();
                        initFormData(formBean);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        mDialog.dismiss();
                    }
                });
    }

    /**
     * 初始化
     *
     * @param formBean
     */
    private void initFormData(FormBean formBean) {
        if (formBean.getOffermoney() != null && formBean.getOfferday() != null) {
            totalMoney.setText("总价：" + formBean.getOffermoney() + "元");
            totalNumber.setText("总周期：" + formBean.getOfferday() + "天");
        } else {
            totalMoney.setText("总价： 0元");
            totalNumber.setText("总周期：0天");
        }
        List<FormBean.ListBean> list = formBean.getList();
        if (list != null) {
            for (FormBean.ListBean bean : list) {
                if (bean.getName().equals("锄草")) {
                    offerday1.setText(bean.getDay());
                    offermoney1.setText(bean.getMoney());

                }
                if (bean.getName().equals("化肥")) {
                    offerday2.setText(bean.getDay());
                    offermoney2.setText(bean.getMoney());

                }
                if (bean.getName().equals("护理")) {
                    offerday3.setText(bean.getDay());
                    offermoney3.setText(bean.getMoney());

                }

            }
        } else {
            offerday1.setText("0");
            offermoney1.setText("0");
            offerday2.setText("0");
            offermoney2.setText("0");
            offerday3.setText("0");
            offermoney3.setText("0");
        }
    }


    @OnClick({R.id.id_title_left,R.id.task_finish})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_title_left:
                finish();
                break;
            case R.id.task_finish:
                selectionUser(uid);
                break;
        }
    }

    /**
     * 选标，uid--农机主的id
     *
     * @param mUid
     */
    private void selectionUser(int mUid) {
        LogUtil.e("TAG",mUid+"");
        // 拼接参数
        OkGo.post(Urls.URL_JOINOWNER_SELECTION)
                .tag(this)
                .params("uid", mUid)
                .params("taskid", taskid)
                .execute(new JsonCallback<ServerResult>() {
                    @Override
                    public void onSuccess(ServerResult serverResult, Call call, Response response) {
                        ToastUtil.showShort(mContext, serverResult.msg);
                        startActivity(MyReleaseTaskActivity.class);
                        finish();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.showShort(mContext, "选标失败");
                    }
                });
    }
}
