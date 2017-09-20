package com.rifeng.agriculturalstation.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.rifeng.agriculturalstation.BaseActivity;
import com.rifeng.agriculturalstation.R;
import com.rifeng.agriculturalstation.adapter.FormAdapter;
import com.rifeng.agriculturalstation.bean.BidBean;
import com.rifeng.agriculturalstation.callback.JsonCallback;
import com.rifeng.agriculturalstation.recyclerview.DividerItemDecoration;
import com.rifeng.agriculturalstation.utils.CommonAdapter;
import com.rifeng.agriculturalstation.utils.CustomProgressDialog;
import com.rifeng.agriculturalstation.utils.ToastUtil;
import com.rifeng.agriculturalstation.utils.Urls;
import com.rifeng.agriculturalstation.utils.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/9/18.
 */

public class TaskTouBiaoActivity extends BaseActivity implements FormAdapter.SaveEditListener {

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
    private List<Integer> offermoneyList = new ArrayList<>();
    private List<Integer> offerdayList = new ArrayList<>();
    private CustomProgressDialog mDialog;

    private int taskid;
    private float joinmoney;
    private List<BidBean> bidBeanList = new ArrayList<>();
    private FormAdapter formAdapter;

    private String money;
    private String day;


    @Override
    protected int getContentViewId() {
        return R.layout.activity_task_select;
    }

    @Override
    protected void initData() {
        Bundle bundle = this.getIntent().getExtras();
        taskid = bundle.getInt("taskid");
        joinmoney = bundle.getFloat("joinmoney");
        mDialog = new CustomProgressDialog(mContext, "正在加载中...");
        idTitleMiddle.setText("参与投标");
        Log.e(TAG, "taskid: "+taskid);
        getTouBiaoData(true);
    }

    private void getTouBiaoData(boolean isShow) {
        if (isShow) {
            mDialog.show();
        }
        OkGo.post(Urls.URL_FORM_NAME+"?taskid="+taskid)
                .tag(this)
                .execute(new JsonCallback<List<BidBean>>() {

                    @Override
                    public void onSuccess(List<BidBean> bidBeen, Call call, Response response) {
                        mDialog.dismiss();
                        if (bidBeen.size() > 0) {
                            bidBeanList.addAll(bidBeen);
                        }
                        setData();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        mDialog.dismiss();
                    }
                });

    }

    /**
     * 设置数据
     */
    private void setData() {
        formAdapter = new FormAdapter(mContext,bidBeanList);
        bidSelectList.setAdapter(formAdapter);
        bidSelectList.setLayoutManager(new LinearLayoutManager(mContext));
        bidSelectList.addItemDecoration(new DividerItemDecoration(mContext,DividerItemDecoration.VERTICAL_LIST));
    }

    @Override
    public void SaveEditDay(int position, String string) {
        if (string!=null&&!string.equals("")) {
            offerdayList.add(Integer.valueOf(string));
        }
    }

    @Override
    public void SaveEditMoney(int position, String string) {
        if (string!=null&&!string.equals("")) {
            offermoneyList.add(Integer.valueOf(string));
        }
    }


    @OnClick({R.id.id_title_left, R.id.task_finish})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_title_left:
                finish();
                break;
            case R.id.task_finish:

//                if (checkParams()) {
                    startPayActivity();
//                }
                for (int i = 0; i < offermoneyList.size(); i++) {
                    Log.e(TAG, "day"+i+":"+offermoneyList.get(i) );

                }
                for (int i = 0; i < offerdayList.size(); i++) {
                    Log.e(TAG, "money"+i+":"+offerdayList.get(i) );
                }
                break;
        }

    }

    private void startPayActivity() {
        Bundle bundle = new Bundle();
        bundle.putInt("taskid", taskid);
        bundle.putFloat("joinmoney", joinmoney);
        bundle.putIntegerArrayList("offermoneyList", (ArrayList<Integer>) offermoneyList);
        bundle.putIntegerArrayList("offerdayList", (ArrayList<Integer>) offerdayList);
        startActivity(TouBiaoPayActivity.class, bundle);
        finish();
    }

}
