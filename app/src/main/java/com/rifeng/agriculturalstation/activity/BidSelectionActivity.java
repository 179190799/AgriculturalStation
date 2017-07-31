package com.rifeng.agriculturalstation.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.rifeng.agriculturalstation.BaseActivity;
import com.rifeng.agriculturalstation.R;
import com.rifeng.agriculturalstation.bean.ServerResult;
import com.rifeng.agriculturalstation.bean.UserBean;
import com.rifeng.agriculturalstation.callback.JsonCallback;
import com.rifeng.agriculturalstation.recyclerview.CommonAdapter;
import com.rifeng.agriculturalstation.recyclerview.base.ViewHolder;
import com.rifeng.agriculturalstation.recyclerview.wrapper.HeaderAndFooterWrapper;
import com.rifeng.agriculturalstation.recyclerview.wrapper.LoadMoreWrapper;
import com.rifeng.agriculturalstation.utils.CustomProgressDialog;
import com.rifeng.agriculturalstation.utils.ToastUtil;
import com.rifeng.agriculturalstation.utils.Urls;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 选标列表
 * <p/>
 * Created by chw on 2016/11/29.
 */
public class BidSelectionActivity extends BaseActivity {

    @BindView(R.id.id_title_left)
    TextView idTitleLeft;
    @BindView(R.id.id_title_middle)
    TextView idTitleMiddle;
    @BindView(R.id.bid_recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.bid_swipeRefresh)
    SwipeRefreshLayout mSwipeRefresh;

    private ImageLoader imageLoader = ImageLoader.getInstance();
    private ArrayList<UserBean> taskList = new ArrayList<>();
    private CommonAdapter<UserBean> mAdapter;
    private HeaderAndFooterWrapper mHeaderAndFooterWrapper;
    private LoadMoreWrapper mLoadMoreWrapper;

    private int taskid;
    private List<UserBean> userList = new ArrayList<>();
    private int start = 0;
    private int perpage = 20;
    private int isLoading = 0;
    private CustomProgressDialog dialog;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_bidselection;
    }

    @Override
    protected void initData() {
        dialog = new CustomProgressDialog(this, "正在加载...");
        idTitleMiddle.setText("选标");
        taskid = this.getIntent().getExtras().getInt("taskid");

        getDatas(true);
        initialize();
    }

    private void initialize() {
        mSwipeRefresh.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        mAdapter = new CommonAdapter<UserBean>(this, R.layout.item_biduser, userList) {
            @Override
            protected void convert(ViewHolder holder, final UserBean userBean, int position) {
                imageLoader.displayImage(Urls.SERVER + userBean.getAvatar(), (ImageView) holder.getView(R.id.bid_user_avatar));
                holder.setText(R.id.bid_user_name, "农机主：" + userBean.getUsername());
                holder.setText(R.id.bid_user_phone, "手机：" + userBean.getPhone());
                holder.setText(R.id.bid_user_address, "地址：" + userBean.getResideaddress());

                // 选择此人
                holder.getView(R.id.bid_user_selection).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectionUser(userBean.getUid());
                    }
                });

                // 查看信息
                holder.getView(R.id.bid_user_info).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("userbean", userBean);
                        startActivity(BasePersonalInfoActivity.class, bundle);
                    }
                });
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
                            getDatas(false);
                            Log.e(TAG, "isLoading  "+isLoading );
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

//                        if (taskList.size() != 0) {
//
//                            taskList.clear();
//
//                        }
                        if (userList.size() != 0) {

                            userList.clear();
                            Log.e(TAG, "isclear:222222222222222 ");
                        }
                        start = 1;
                        getDatas(true);
                        mSwipeRefresh.setRefreshing(false);
                    }
                }, 2000);
            }
        });
    }

    /**
     * 选标，uid--农机主的id
     *
     * @param uid
     */
    private void selectionUser(int uid) {
        // 拼接参数
        OkGo.post(Urls.URL_JOINOWNER_SELECTION)
                .tag(this)
                .params("uid", uid)
                .params("taskid", taskid)
                .execute(new JsonCallback<ServerResult>() {
                    @Override
                    public void onSuccess(ServerResult serverResult, Call call, Response response) {
                        ToastUtil.showShort(BidSelectionActivity.this, serverResult.msg);
                        startActivity(MyReleaseTaskActivity.class);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.showShort(mContext, "选标失败");
                    }
                });
    }

    /**
     * 获取投标人列表
     *
     * @param isShow
     */
    private void getDatas(boolean isShow) {
        if (isShow) {
            dialog.show();
        }
        // 拼接参数
        OkGo.post(Urls.URL_GET_JOINOWNER_LIST)
                .tag(this)
                .params("taskid", taskid)
                .params("start", start)
                .params("perpage", perpage)
                .execute(new JsonCallback<List<UserBean>>() {
                    @Override
                    public void onSuccess(List<UserBean> userBeens, Call call, Response response) {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        if (userBeens != null) {
                            isLoading = userBeens.size();
                            if (isLoading > 0) {
                                userList.addAll(userBeens);
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

    @OnClick({R.id.id_title_left})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_title_left: // 返回
                finish();
                break;
        }
    }
}
