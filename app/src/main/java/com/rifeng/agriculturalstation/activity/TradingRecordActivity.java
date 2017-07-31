package com.rifeng.agriculturalstation.activity;

import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.request.BaseRequest;
import com.rifeng.agriculturalstation.BaseActivity;
import com.rifeng.agriculturalstation.R;
import com.rifeng.agriculturalstation.bean.TradingRecordBean;
import com.rifeng.agriculturalstation.callback.JsonCallback;
import com.rifeng.agriculturalstation.recyclerview.CommonAdapter;
import com.rifeng.agriculturalstation.recyclerview.base.ViewHolder;
import com.rifeng.agriculturalstation.recyclerview.wrapper.HeaderAndFooterWrapper;
import com.rifeng.agriculturalstation.recyclerview.wrapper.LoadMoreWrapper;
import com.rifeng.agriculturalstation.utils.Consts;
import com.rifeng.agriculturalstation.utils.CustomProgressDialog;
import com.rifeng.agriculturalstation.utils.DateUtil;
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
 * 交易记录
 * <p>
 * Created by chw on 2017/1/18.
 */
public class TradingRecordActivity extends BaseActivity {

    @BindView(R.id.id_title_left)
    TextView getBack;
    @BindView(R.id.id_title_middle)
    TextView middleTitle;
    @BindView(R.id.trading_recycler)
    RecyclerView mRecyclerView;
    @BindView(R.id.trading_swiperefresh)
    SwipeRefreshLayout mSwipeRefresh;

    private ArrayList<TradingRecordBean> tradingList = new ArrayList<>();
    private CommonAdapter<TradingRecordBean> mAdapter;
    private HeaderAndFooterWrapper mHeaderAndFooterWrapper;
    private LoadMoreWrapper mLoadMoreWrapper;
    private CustomProgressDialog dialog;
    private int start = 0;
    private int perpage = 20;
    private int isLoading = 0;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_tradingrecord;
    }

    @Override
    protected void initData() {
        middleTitle.setText("交易记录");
        dialog = new CustomProgressDialog(this, "正在加载...");

        addDatas(true);
        initialize();
    }

    private void initialize() {
        mSwipeRefresh.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new CommonAdapter<TradingRecordBean>(this, R.layout.item_tradingrecord, tradingList) {

            @Override
            protected void convert(ViewHolder holder, TradingRecordBean tradingRecordBean, int position) {
                holder.setText(R.id.designation, tradingRecordBean.designation);
                holder.setText(R.id.dateline, DateUtil.getTime(tradingRecordBean.dateline, "yyyy-MM-dd"));
                holder.setText(R.id.operation, "操作：" + tradingRecordBean.operation);
                holder.setText(R.id.money, tradingRecordBean.money + "￥");
                TextView stateTV = holder.getView(R.id.state);
                if (tradingRecordBean.state == 0) {
                    stateTV.setText("未付款");
                    stateTV.setTextColor(TradingRecordActivity.this.getResources().getColor(R.color.colorAccent));
                } else {
                    stateTV.setText("已付款");
                    stateTV.setTextColor(TradingRecordActivity.this.getResources().getColor(R.color.colorGreenDark));
                }
            }
        };

        mHeaderAndFooterWrapper = new HeaderAndFooterWrapper(mAdapter);
        mLoadMoreWrapper = new LoadMoreWrapper(mHeaderAndFooterWrapper);
        mLoadMoreWrapper.setLoadMoreView(0);
        mLoadMoreWrapper.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isLoading > 0) {
                            addDatas(false);
                        }
                    }
                }, 2000);
            }
        });

        // 设置适配器数据
        mRecyclerView.setAdapter(mLoadMoreWrapper);
        // 设置item点击事件
        mAdapter.setOnItemClickListener(new CommonAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                mAdapter.notifyItemRemoved(position);
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });

        // 下拉刷新
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (tradingList.size() != 0) {
                            tradingList.clear();
                        }
                        start = 0;
                        addDatas(true);
                        mSwipeRefresh.setRefreshing(false);
                    }
                }, 2000);
            }
        });
    }

    private void addDatas(boolean isShow) {
        if (isShow) {
            dialog.show();
        }

        // 拼接参数
        OkGo.post(Urls.URL_TRADING_LIST)
            .tag(this)
            .params("uid", (int) SharedPreferencesUtil.get(mContext, Consts.USER_UID, 0), true)
            .params("start", start)
            .params("perpage", perpage)
            .execute(new JsonCallback<List<TradingRecordBean>>() {
                @Override
                public void onBefore(BaseRequest request) {
                    super.onBefore(request);
                }

                @Override
                public void onSuccess(List<TradingRecordBean> tradingRecordBeanList, Call call, Response response) {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    if(tradingRecordBeanList != null){
                        isLoading = tradingRecordBeanList.size();
                        if(isLoading > 0){
                            tradingList.addAll(tradingRecordBeanList);
                            start += isLoading;
                        }
                        if (isLoading < perpage) {
                            mLoadMoreWrapper.setLoadMoreView(0);
                        }else {
                            mLoadMoreWrapper.setLoadMoreView(R.layout.default_loading);
                        }
                    }

                    mLoadMoreWrapper.notifyDataSetChanged();
                }

                @Override
                public void onError(Call call, Response response, Exception e) {
                    super.onError(call, response, e);
                    ToastUtil.showShort(mContext, "加载失败");
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            });
    }

    @OnClick(R.id.id_title_left)
    public void onClick() {
        finish();
    }
}
