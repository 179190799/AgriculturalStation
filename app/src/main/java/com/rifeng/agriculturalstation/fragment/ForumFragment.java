package com.rifeng.agriculturalstation.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.rifeng.agriculturalstation.BaseFragment;
import com.rifeng.agriculturalstation.R;
import com.rifeng.agriculturalstation.activity.ForumDetailsActivity;
import com.rifeng.agriculturalstation.bean.ForumBean;
import com.rifeng.agriculturalstation.callback.JsonCallback;
import com.rifeng.agriculturalstation.recyclerview.CommonAdapter;
import com.rifeng.agriculturalstation.recyclerview.DividerItemDecoration;
import com.rifeng.agriculturalstation.recyclerview.base.ViewHolder;
import com.rifeng.agriculturalstation.recyclerview.wrapper.HeaderAndFooterWrapper;
import com.rifeng.agriculturalstation.recyclerview.wrapper.LoadMoreWrapper;
import com.rifeng.agriculturalstation.utils.Consts;
import com.rifeng.agriculturalstation.utils.CustomProgressDialog;
import com.rifeng.agriculturalstation.utils.DateUtil;
import com.rifeng.agriculturalstation.utils.LogUtil;
import com.rifeng.agriculturalstation.utils.SharedPreferencesUtil;
import com.rifeng.agriculturalstation.utils.ToastUtil;
import com.rifeng.agriculturalstation.utils.Urls;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 论坛
 * <p>
 * Created by chw on 2016/10/18.
 */
public class ForumFragment extends BaseFragment {

    @BindView(R.id.id_title_left)
    TextView idTitleLeft;
    @BindView(R.id.id_title_middle)
    TextView idTitleMiddle;
    @BindView(R.id.forum_recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R.id.forum_swiperefresh)
    SwipeRefreshLayout mSwipeRefresh;

    private ArrayList<ForumBean> forumList = new ArrayList<>();
    private CommonAdapter<ForumBean> mAdapter;
    private HeaderAndFooterWrapper mHeaderAndFooterWrapper;
    private LoadMoreWrapper mLoadMoreWrapper;

    private CustomProgressDialog dialog;
    private int start = 0;
    private int perpage = 9;
    private int isLoading = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_forum;
    }

    @Override
    protected void initData() {

        dialog = new CustomProgressDialog(getContext(), "正在加载...");
        idTitleLeft.setCompoundDrawables(null, null, null, null);
        idTitleMiddle.setText("帖子");


        initialize();

        int uid = (int) SharedPreferencesUtil.get(getActivity(), Consts.USER_UID, 0);
        Log.e("TAG", "------------- uid: -------------"+uid);


    }

    /**
     * 当Fragment对用户可见（即用户切换到此Fragment时）执行此方法
     */
    @Override
    protected void lazyLoad() {
        if (!isVisible) {
            return;
        } else {
            LogUtil.i("TAG", "ForumFragment --- UI布局可见");
            addDatas(false);
        }
    }

    private void initialize() {

        mSwipeRefresh.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // 添加分隔线
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        mAdapter = new CommonAdapter<ForumBean>(getActivity(), R.layout.item_forum, forumList) {

            @Override
            protected void convert(ViewHolder holder, ForumBean forumBean, int position) {
                switch (forumBean.type) {
                    case 1:
                        holder.setText(R.id.forum_list_type, "[置顶]");
                        break;

                    case 2:
                        holder.setText(R.id.forum_list_type, "[热帖]").setTextColor(R.id.forum_list_type, Color.RED);
                        break;

                    default:
                        holder.setText(R.id.forum_list_type, "[新帖]").setTextColor(R.id.forum_list_type, Color.GRAY);
                        break;
                }
                holder.setText(R.id.forum_list_title, forumBean.name);

                holder.setText(R.id.forum_list_views, "浏览(" + forumBean.viewnum + ")");
                holder.setText(R.id.forum_list_time, DateUtil.getTime(forumBean.dateline + "", "yyyy-MM-dd"));
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
                bundle.putSerializable("ForumBean", forumList.get(position));
                Intent intent = new Intent(getActivity(), ForumDetailsActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);

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
                        if (forumList.size() != 0) {
                            forumList.clear();
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
        OkGo.post(Urls.URL_FORUM_LIST)
                .tag(this)
                .params("uid", (int) SharedPreferencesUtil.get(getActivity(), Consts.USER_UID, 0))
                .params("start", start)
                .params("perpage", perpage)
                .execute(new JsonCallback<List<ForumBean>>() {
                    @Override
                    public void onSuccess(List<ForumBean> forumBeanList, Call call, Response response) {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        isLoading = forumBeanList.size();

                        if (isLoading > 0) {
                            forumList.addAll(forumBeanList);

                            start += isLoading;
                        }
                        if (isLoading < perpage) {
                            mLoadMoreWrapper.setLoadMoreView(0);
                        } else {
                            mLoadMoreWrapper.setLoadMoreView(R.layout.default_loading);
                        }
                        mLoadMoreWrapper.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.showShort(getActivity(), "加载失败");
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });
    }
}
