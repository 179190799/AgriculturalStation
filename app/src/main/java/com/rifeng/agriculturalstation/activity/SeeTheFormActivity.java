package com.rifeng.agriculturalstation.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.rifeng.agriculturalstation.BaseActivity;
import com.rifeng.agriculturalstation.R;
import com.rifeng.agriculturalstation.adapter.FormAdapter;
import com.rifeng.agriculturalstation.adapter.FormAdapter2;
import com.rifeng.agriculturalstation.bean.FormBean;
import com.rifeng.agriculturalstation.bean.ServerResult;
import com.rifeng.agriculturalstation.callback.JsonCallback;
import com.rifeng.agriculturalstation.recyclerview.DividerItemDecoration;
import com.rifeng.agriculturalstation.recyclerview.wrapper.HeaderAndFooterWrapper;
import com.rifeng.agriculturalstation.recyclerview.wrapper.LoadMoreWrapper;
import com.rifeng.agriculturalstation.utils.CommonAdapter;
import com.rifeng.agriculturalstation.utils.CustomProgressDialog;
import com.rifeng.agriculturalstation.utils.LogUtil;
import com.rifeng.agriculturalstation.utils.ToastUtil;
import com.rifeng.agriculturalstation.utils.Urls;
import com.rifeng.agriculturalstation.utils.ViewHolder;

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
    @BindView(R.id.bid_select_list)
    RecyclerView bidSelectList;
    @BindView(R.id.task_finish)
    TextView taskFinish;

    private FormAdapter2 formAdapter;
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

        Log.e(TAG, "taskid: " + taskid);
        Log.e(TAG, "uid: " + uid);

        getFormData(true);

    }

    /**
     * 获取表单数据
     *
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
        formAdapter = new FormAdapter2(mContext,list);
        bidSelectList.setAdapter(formAdapter);
        bidSelectList.setLayoutManager(new LinearLayoutManager(mContext));
        bidSelectList.addItemDecoration(new DividerItemDecoration(mContext,DividerItemDecoration.VERTICAL_LIST));
    }


    @OnClick({R.id.id_title_left, R.id.task_finish})
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
        LogUtil.e("TAG", mUid + "");
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
