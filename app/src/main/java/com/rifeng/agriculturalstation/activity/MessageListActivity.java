package com.rifeng.agriculturalstation.activity;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.request.BaseRequest;
import com.rifeng.agriculturalstation.BaseActivity;
import com.rifeng.agriculturalstation.R;
import com.rifeng.agriculturalstation.bean.MyMessageBean;
import com.rifeng.agriculturalstation.callback.JsonCallback;
import com.rifeng.agriculturalstation.recyclerview.CommonAdapter;
import com.rifeng.agriculturalstation.recyclerview.DividerItemDecoration;
import com.rifeng.agriculturalstation.recyclerview.base.ViewHolder;
import com.rifeng.agriculturalstation.recyclerview.wrapper.HeaderAndFooterWrapper;
import com.rifeng.agriculturalstation.recyclerview.wrapper.LoadMoreWrapper;
import com.rifeng.agriculturalstation.utils.AsyncHttpUtil;
import com.rifeng.agriculturalstation.utils.Consts;
import com.rifeng.agriculturalstation.utils.CustomProgressDialog;
import com.rifeng.agriculturalstation.utils.DateUtil;
import com.rifeng.agriculturalstation.utils.SharedPreferencesUtil;
import com.rifeng.agriculturalstation.utils.ToastUtil;
import com.rifeng.agriculturalstation.utils.Urls;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 个人中心-我的消息
 * <p>
 * Created by chw on 2016/10/25.
 */
public class MessageListActivity extends BaseActivity {

    @BindView(R.id.id_title_left)
    TextView idTitleLeft;
    @BindView(R.id.id_title_middle)
    TextView idTitleMiddle;
    @BindView(R.id.id_title_right)
    TextView idTitleRight; // 编辑
    @BindView(R.id.mymsg_recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R.id.mymsg_swiperefresh)
    SwipeRefreshLayout mSwipeRefresh;

    private List<MyMessageBean> msgList = new ArrayList<>();
    private CommonAdapter<MyMessageBean> mAdapter;
    private HeaderAndFooterWrapper mHeaderAndFooterWrapper;
    private LoadMoreWrapper mLoadMoreWrapper;

    private boolean flag = false;
    private List<Integer> mPos = new ArrayList<>(); // 用于记录选中的CheckBox
    private List<Integer> idList = new ArrayList<>(); // 存放选中的记录的id
    private CustomProgressDialog dialog;
    private int start = 0;
    private int perpage = 20;
    private int isLoading = 0;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_messagelist;
    }

    @Override
    protected void initData() {
        dialog = new CustomProgressDialog(mContext, "拼命加载中...");
        idTitleMiddle.setText("我的消息");
        idTitleRight.setText("编辑");
        // 获取用户数据
        addDatas(true);

        mSwipeRefresh.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // 添加分隔线
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        mAdapter = new CommonAdapter<MyMessageBean>(this, R.layout.item_messagelist, msgList) {
            @Override
            protected void convert(final ViewHolder holder, final MyMessageBean myMessageBean, final int position) {
                holder.setText(R.id.mymsg_title, myMessageBean.getTitle());
                holder.setText(R.id.mymsg_time, DateUtil.getTime(myMessageBean.getDateline() + "", "yyyy-MM-dd"));
                holder.setText(R.id.mymsg_content, myMessageBean.getContent());
                final CheckBox cb = holder.getView(R.id.mymsg_cb);
                if (flag) {
//                    id_title_middle.setText("删除");

                    // 检测是否有选中checkbox的item
                    cbChangeDelete();

                    holder.setVisible(R.id.mymsg_cb, true);
                    cb.setChecked(false);

                    if (mPos.contains(position)) {

                        cb.setChecked(true);
                    }

                    cb.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (cb.isChecked()) {
                                idList.add(myMessageBean.getId());
//                                idSetList.add(1);
                                mPos.add(position);
                            } else {
                                idList.remove((Integer) myMessageBean.getId());
                                mPos.remove((Integer) position);
                            }
                            // 检测是否有选中checkbox的item
                            cbChangeDelete();
                        }
                    });

                } else {
                    mPos.clear();
                    holder.setVisible(R.id.mymsg_cb, false);
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
                if (!flag) { // 当前为取消状态
                    ToastUtil.showShort(mContext, "当前为取消状态...");
                    Bundle bundle = new Bundle();
                    bundle.putString("flag", "Msg");
                    bundle.putSerializable("msgBean", msgList.get(position));
                    startActivity(TradeAlertsDetailsActivity.class, bundle);

                }
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
                        if (msgList.size() != 0) {
                            msgList.clear();
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
        OkGo.post(Urls.URL_MYNEWS_LIST)
            .tag(this)
            .params("uid", (int) SharedPreferencesUtil.get(mContext, Consts.USER_UID, 0), true)
            .params("start", start)
            .params("perpage", perpage)
            .execute(new JsonCallback<List<MyMessageBean>>() {
                @Override
                public void onSuccess(List<MyMessageBean> myMessageBeanList, Call call, Response response) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    if(myMessageBeanList != null){
                        isLoading = myMessageBeanList.size();
                        if(isLoading > 0){
                            msgList.addAll(myMessageBeanList);
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
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            });
    }

    /**
     * 检测是否有选中checkbox的item
     */
    private void cbChangeDelete() {
        if (mPos.size() > 0) { // 选中的item
            idTitleMiddle.setClickable(true);
            idTitleMiddle.setTextColor(Color.WHITE);
        } else { // 一个也没有选中
            idTitleMiddle.setClickable(false);
            idTitleMiddle.setTextColor(Color.parseColor("#CCCCCC"));
        }

        if (idTitleMiddle.isClickable()) { // 删除
            idTitleMiddle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteMsg();
                }
            });
        }
    }

    /**
     * 删除选中的记录
     */
    private void deleteMsg() {
        dialog.show();

        RequestParams params = new RequestParams();
        params.put("uid", SharedPreferencesUtil.get(mContext, Consts.USER_UID, 0));
        params.put("checkpos", idList);

        AsyncHttpUtil.post(Urls.URL_MYNEWS_DELETE, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    ToastUtil.showShort(mContext, response.getString("msg"));
                    dialog.dismiss();
                    if(response.getInt("code") == 200){
                        // 移除删除了的选项
                        for (int i = 0; i < idList.size(); i++) {
                            for (int k = 0; k < msgList.size(); k++) {
                                if (msgList.get(k).getId() == idList.get(i)) {
                                    msgList.remove(msgList.get(k));
                                }
                            }
                        }
                        // 清空保存选择的position
                        if (mPos != null) {
                            mPos.clear();
                        }
                        // 刷新列表数据
                        mAdapter.notifyDataSetChanged();
                        mLoadMoreWrapper.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                dialog.dismiss();
            }
        });
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

    @OnClick({R.id.id_title_left, R.id.id_title_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_title_left:
                if (flag) { // 全选
                    // 清空记录选中的item
                    if (idList != null) {
                        idList.clear();
                    }
                    for (int i = 0; i < start; i++) {
                        mPos.add(i);
                        idList.add(msgList.get(i).getId());
                    }
                    mLoadMoreWrapper.notifyDataSetChanged();
                } else { // 返回
                    defaultFinish();
                }

                break;

            case R.id.id_title_right: // 编辑
                if (!flag) { // 当前为编辑状态
                    idTitleLeft.setCompoundDrawables(null, null, null, null);
                    idTitleLeft.setText("全选");

                    idTitleMiddle.setText("删除");
                    idTitleMiddle.setTextSize(16);
                    idTitleMiddle.setTextColor(Color.parseColor("#CCCCCC"));

                    idTitleRight.setText("取消");

                    mLoadMoreWrapper.notifyDataSetChanged();
                    flag = true;
                } else { // 当前为取消状态
                    // 清空记录选中的item
                    if (idList != null) {
                        idList.clear();
                    }
                    Drawable drawableLeft = mContext.getResources().getDrawable(R.mipmap.common_title_left);
                    // 必须设置图片大小，否则不显示
                    drawableLeft.setBounds(0, 0, drawableLeft.getMinimumWidth(), drawableLeft.getMinimumHeight());

                    idTitleLeft.setCompoundDrawables(drawableLeft, null, null, null);
                    idTitleLeft.setText("");

                    idTitleMiddle.setText("我的消息");
                    idTitleMiddle.setTextSize(20);
                    idTitleMiddle.setTextColor(Color.WHITE);

                    idTitleRight.setText("编辑");
                    mLoadMoreWrapper.notifyDataSetChanged();
                    flag = false;
                }
                break;
        }
    }
}
