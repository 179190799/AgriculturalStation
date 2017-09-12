package com.rifeng.agriculturalstation.activity;


import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.rifeng.agriculturalstation.BaseActivity;
import com.rifeng.agriculturalstation.R;
import com.rifeng.agriculturalstation.bean.PayStagesBean;
import com.rifeng.agriculturalstation.bean.ServerResult;
import com.rifeng.agriculturalstation.bean.StagesPayBean;
import com.rifeng.agriculturalstation.callback.JsonCallback;
import com.rifeng.agriculturalstation.recyclerview.CommonAdapter;
import com.rifeng.agriculturalstation.recyclerview.base.ViewHolder;
import com.rifeng.agriculturalstation.recyclerview.wrapper.HeaderAndFooterWrapper;
import com.rifeng.agriculturalstation.recyclerview.wrapper.LoadMoreWrapper;
import com.rifeng.agriculturalstation.utils.Consts;
import com.rifeng.agriculturalstation.utils.SharedPreferencesUtil;
import com.rifeng.agriculturalstation.utils.ToastUtil;
import com.rifeng.agriculturalstation.utils.Urls;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 *
 * 支付进度款
 *
 */
public class PayStagesActivity extends BaseActivity {

    @BindView(R.id.id_title_left)
    TextView idTitleLeft;
    @BindView(R.id.id_title_middle)
    TextView idTitleMiddle;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private CommonAdapter<PayStagesBean> mAdapter;
    private HeaderAndFooterWrapper mHeaderAndFooterWrapper;
    private LoadMoreWrapper mLoadMoreWrapper;
    private ArrayList<StagesPayBean> stagesPayList = new ArrayList<>();

    //接收传递过来的
    private List<PayStagesBean> payStagesList = new ArrayList<>();

    private int taskId;
    private int payWay = 1; //支付方式：1 支付宝（默认）  2 微信  3 余额
    private int regtype;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_pay_stages;
    }

    @Override
    protected void initData() {
        idTitleMiddle.setText("支付进度款");
        taskId = this.getIntent().getExtras().getInt("taskid", 0);
        stagesPayList = this.getIntent().getExtras().getParcelableArrayList("stagesPayList");
        payStagesList = (List<PayStagesBean>) this.getIntent().getSerializableExtra("payStagesList");
        Log.e("TAG", "payStagesBeanList: "+payStagesList );
        regtype = (int) SharedPreferencesUtil.get(this, Consts.USER_REGTYPE, 0);
//        initialize();
        initializeTrue();
    }


//    private void initialize() {
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        mAdapter = new CommonAdapter<StagesPayBean>(this, R.layout.item_pay_stages, stagesPayList) {
//
//            @Override
//            protected void convert(ViewHolder holder, final StagesPayBean stagesPayBean, int position) {
//                holder.setText(R.id.pay_stages_num, "第" + stagesPayBean.stages + "期");
//                holder.setText(R.id.pay_stages_price, String.valueOf(stagesPayBean.money));
//
//                TextView payStatus = holder.getView(R.id.pay_stages_status);
//                payStatus.setClickable(true);
//                switch (stagesPayBean.status){ // 1 已付清  0 支付  2 未到期
//                    case 0:{
//                        if(regtype == 1){
//                            payStatus.setText("支付");
//                            payStatus.setBackgroundResource(R.drawable.input_shape);
//                            payStatus.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    // 显示支付方式
//                                    showPayWay(stagesPayBean.money, stagesPayBean.id);
//                                }
//                            });
//                        }else if(regtype == 2){
//                            payStatus.setText("未付清");
//                            payStatus.setClickable(false);
//                        }
//                    }
//                        break;
//
//                    case 1:
//                        payStatus.setText("已付清");
//                        payStatus.setClickable(false);
//                        break;
//
//                    case 2:
//                        if(regtype == 1){
//                            payStatus.setText("未到期");
//                            payStatus.setClickable(false);
//                        }else if(regtype == 2){
//                            payStatus.setText("未付清");
//                            payStatus.setClickable(false);
//                        }
//                        break;
//                }
//            }
//        };
//
//        mHeaderAndFooterWrapper = new HeaderAndFooterWrapper(mAdapter);
//        mLoadMoreWrapper = new LoadMoreWrapper(mHeaderAndFooterWrapper);
//        // 设置适配器数据
//        mRecyclerView.setAdapter(mLoadMoreWrapper);
//    }

    private void initializeTrue() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new CommonAdapter<PayStagesBean>(this, R.layout.item_pay_stages, payStagesList) {

            @Override
            protected void convert(ViewHolder holder, final PayStagesBean payStagesBean, int position) {
                holder.setText(R.id.pay_stages_num, "第" + payStagesBean.getStages() + "期");
                holder.setText(R.id.pay_stages_price, String.valueOf(payStagesBean.getMoney()));

                TextView payStatus = holder.getView(R.id.pay_stages_status);
                payStatus.setClickable(true);
                switch (payStagesBean.getStatus()){ // 1 已付清  0 支付  2 未到期
                    case 0:{
                        if(regtype == 1){
                            payStatus.setText("支付");
                            payStatus.setBackgroundResource(R.drawable.input_shape);
                            payStatus.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // 显示支付方式
//                                    showPayWay(stagesPayBean.money, stagesPayBean.id);
                                    Bundle bundle = new Bundle();
                                    bundle.putInt("id",payStagesBean.getId());
                                    bundle.putInt("taskid",taskId);
                                    bundle.putString("money",payStagesBean.getMoney());
                                    startActivity(PayStagesPayWayActivity.class, bundle);
                                    finish();
//                                    ToastUtil.showShort(mContext,"您点击了支付！");
                                }
                            });
                        }else if(regtype == 2){
                            payStatus.setText("未付清");
                            payStatus.setClickable(false);
                        }
                    }
                    break;

                    case 1:
                        payStatus.setText("已付清");
                        payStatus.setClickable(false);
                        break;

                    case 2:
                        if(regtype == 1){
                            payStatus.setText("未到期");
                            payStatus.setClickable(false);
                        }else if(regtype == 2){
                            payStatus.setText("未付清");
                            payStatus.setClickable(false);
                        }
                        break;
                }
            }
        };

        mHeaderAndFooterWrapper = new HeaderAndFooterWrapper(mAdapter);
        mLoadMoreWrapper = new LoadMoreWrapper(mHeaderAndFooterWrapper);
        // 设置适配器数据
        mRecyclerView.setAdapter(mLoadMoreWrapper);
    }

    /**
     * 显示支付方式
     */
    private void showPayWay(double price, final int stagesid) {
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();  //为获取屏幕宽、高

        View view = getLayoutInflater().inflate(R.layout.dialog_pay_stages, null);
        TextView payPriceTV = (TextView) view.findViewById(R.id.dialog_pay_price);
        Button payBtn = (Button) view.findViewById(R.id.dialog_pay_btn);
        RadioGroup payWayRG = (RadioGroup) view.findViewById(R.id.pay_way_rg);

        payPriceTV.setText("须支付金额：" + String.valueOf(price)  + "￥");

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(view);
        dialog.setTitle("支付进度款");
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.height = (int) (d.getHeight() * 0.8);
        dialog.getWindow().setAttributes(lp);
        dialog.show();

        payWayRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.alipay_rb){
                    payWay = 1;
                }else if(checkedId == R.id.weixin_rb){
                    payWay = 2;
                }else if(checkedId == R.id.balance_rb){
                    payWay = 3;
                }
            }
        });

        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                paySubmit(stagesid);
            }
        });
    }

    private void paySubmit(int stagesid) {
        if(payWay == 1){ // 支付宝
            ToastUtil.showShort(this, "尚未开通该功能，敬请期待");

        }else if(payWay == 2){ // 微信
            ToastUtil.showShort(this, "尚未开通该功能，敬请期待");
        }else if(payWay == 3){ // 余额
            balancePay(stagesid);
        }
    }

    private void balancePay(int stagesid) {
        // 拼接参数
        OkGo.post(Urls.URL_TASK_BALANCE_PAY_STAGES)
            .tag(this)
            .params("stagesid", stagesid)
            .params("uid", (int) SharedPreferencesUtil.get(mContext, Consts.USER_UID, 0))
            .params("taskid", taskId)
            .params("paytype", payWay)
            .execute(new JsonCallback<ServerResult>() {
                @Override
                public void onSuccess(ServerResult serverResult, Call call, Response response) {
                    if(serverResult != null){
                        ToastUtil.showShort(PayStagesActivity.this, serverResult.msg);
                        if(serverResult.code == 200){ // 支付成功
                            finish();
                        }
                    }
                }

                @Override
                public void onError(Call call, Response response, Exception e) {
                    super.onError(call, response, e);
                    ToastUtil.showShort(mContext, "网络错误");
                }
            });
    }

    @OnClick({R.id.id_title_left})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_title_left:
                finish();
                break;
        }
    }
}
