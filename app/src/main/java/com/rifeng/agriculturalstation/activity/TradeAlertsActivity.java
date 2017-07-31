package com.rifeng.agriculturalstation.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.rifeng.agriculturalstation.BaseActivity;
import com.rifeng.agriculturalstation.R;
import com.rifeng.agriculturalstation.bean.TradeAlertsBean;
import com.rifeng.agriculturalstation.callback.JsonCallback;
import com.rifeng.agriculturalstation.recyclerview.CommonAdapter;
import com.rifeng.agriculturalstation.recyclerview.DividerItemDecoration;
import com.rifeng.agriculturalstation.recyclerview.base.ViewHolder;
import com.rifeng.agriculturalstation.recyclerview.wrapper.HeaderAndFooterWrapper;
import com.rifeng.agriculturalstation.recyclerview.wrapper.LoadMoreWrapper;
import com.rifeng.agriculturalstation.utils.CustomProgressDialog;
import com.rifeng.agriculturalstation.utils.DateUtil;
import com.rifeng.agriculturalstation.utils.ToastUtil;
import com.rifeng.agriculturalstation.utils.Urls;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 行业快讯
 * <p>
 * Created by chw on 2016/11/1.
 */
public class TradeAlertsActivity extends BaseActivity {

    @BindView(R.id.id_title_left)
    TextView idTitleLeft;
    @BindView(R.id.id_title_middle)
    TextView idTitleMiddle;
    @BindView(R.id.trade_recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R.id.trade_swiperefresh)
    SwipeRefreshLayout mSwipeRefresh;

    private ArrayList<TradeAlertsBean> tradeList = new ArrayList<>();
    private CommonAdapter<TradeAlertsBean> mAdapter;
    private HeaderAndFooterWrapper mHeaderAndFooterWrapper;
    private LoadMoreWrapper mLoadMoreWrapper;

    private CustomProgressDialog dialog;
    private int start = 0;
    private int perpage = 20;
    private int isLoading = 0;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_tradealerts;
    }

    @Override
    protected void initData() {
        idTitleMiddle.setText("行业快讯");
        dialog = new CustomProgressDialog(this, "正在加载...");
        addDatas(true);
        initialize();
    }

    private void initialize() {
        mSwipeRefresh.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // 添加分隔线
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        mAdapter = new CommonAdapter<TradeAlertsBean>(this, R.layout.item_tradealerts, tradeList) {

            @Override
            protected void convert(ViewHolder holder, TradeAlertsBean tradeAlertsBean, int position) {
                holder.setText(R.id.tradealerts_title, tradeAlertsBean.name);
                holder.setText(R.id.tradealerts_content, String.valueOf(Html.fromHtml(tradeAlertsBean.content)));
                holder.setText(R.id.tradealerts_views, "浏览(" + tradeAlertsBean.viewnum + ")");
                holder.setText(R.id.tradealerts_time, DateUtil.getTime(tradeAlertsBean.dateline + "", "yyyy-MM-dd"));
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

                Bundle bundle = new Bundle(); // 存储数据
                bundle.putString("flag", "Trade");
                bundle.putSerializable("TradeAlertsBean", tradeList.get(position));
                startActivity(TradeAlertsDetailsActivity.class, bundle);
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

                        if (tradeList.size() != 0) {
                            tradeList.clear();
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
        OkGo.post(Urls.URL_TRADEALERTS_LIST)
                .tag(this)
                .params("start", start)
                .params("perpage", perpage)
                .execute(new JsonCallback<List<TradeAlertsBean>>() {
                    @Override
                    public void onSuccess(List<TradeAlertsBean> tradeAlertsBeanList, Call call, Response response) {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        if (tradeAlertsBeanList != null) {
                            isLoading = tradeAlertsBeanList.size();
                            if (isLoading > 0) {
                                tradeList.addAll(tradeAlertsBeanList);
                                start += isLoading;
                            }
                            if (isLoading < perpage) {
                                mLoadMoreWrapper.setLoadMoreView(0);
                            } else {
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

    @OnClick({R.id.id_title_left})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_title_left: // 返回
                finish();
                break;
        }
    }
}
