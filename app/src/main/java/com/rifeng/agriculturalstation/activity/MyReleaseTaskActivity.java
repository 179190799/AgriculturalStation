package com.rifeng.agriculturalstation.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.rifeng.agriculturalstation.BaseActivity;
import com.rifeng.agriculturalstation.R;
import com.rifeng.agriculturalstation.bean.ServerResult;
import com.rifeng.agriculturalstation.bean.TaskBean;
import com.rifeng.agriculturalstation.callback.JsonCallback;
import com.rifeng.agriculturalstation.recyclerview.CommonAdapter;
import com.rifeng.agriculturalstation.recyclerview.DividerItemDecoration;
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
 * 个人中心-发布的任务
 * <p>
 * Created by chw on 2016/10/24.
 */
public class MyReleaseTaskActivity extends BaseActivity {

    @BindView(R.id.id_title_left)
    TextView idTitleLeft;
    @BindView(R.id.id_title_middle)
    TextView idTitleMiddle;
    @BindView(R.id.id_title_right)
    TextView idTitleRight; // 发布任务
    @BindView(R.id.mytask_recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R.id.mytask_swiperefresh)
    SwipeRefreshLayout mSwipeRefresh;

    private ImageLoader imageLoader = ImageLoader.getInstance();
    private CustomProgressDialog dialog;
    private ArrayList<TaskBean> taskList = new ArrayList<>();
    private CommonAdapter<TaskBean> mAdapter;
    private HeaderAndFooterWrapper mHeaderAndFooterWrapper;
    private LoadMoreWrapper mLoadMoreWrapper;

    private int regType; // 1 农场主  2 农机主
    private ObjectAnimator anim; // 属性动画
    private int start = 0;
    private int perpage = 12;
    private int isLoading = 0;


    @Override
    protected int getContentViewId() {
        return R.layout.activity_myreleasetask;
    }

    @Override
    protected void initData() {
        dialog = new CustomProgressDialog(mContext, "拼命加载中...");

        regType = (int) SharedPreferencesUtil.get(mContext, Consts.USER_REGTYPE, 0);
        if (regType == 1) { // 农场主
            idTitleMiddle.setText("发布的任务");

            // 发布任务按钮
            Drawable drawableLeft = mContext.getResources().getDrawable(R.mipmap.add_farm_right);
            // 必须设置图片大小，否则不显示
            drawableLeft.setBounds(0, 0, drawableLeft.getMinimumWidth(), drawableLeft.getMinimumHeight());
            idTitleRight.setCompoundDrawables(drawableLeft, null, null, null);
            /**
             * rotation 表明要执行旋转
             * 属性动画
             */
            anim = ObjectAnimator.ofFloat(idTitleRight, "rotation", 0f, 45f);
            anim.setDuration(200); // 设置动画持续时长
            // 如果只需要知道一种状态，那么可以使用AnimatorListenerAdapter
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    // 发布任务
                    startActivity(ReleaseTaskActivity.class);
                }
            });
        } else if (regType == 2) { // 农机主
            idTitleMiddle.setText("参与的任务");
        }


        initialize();

        // 获取任务列表
        getTaskList(true);
    }



    private void initialize() {
        mSwipeRefresh.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // 添加分隔线
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        mAdapter = new CommonAdapter<TaskBean>(this, R.layout.item_mytask, taskList) {

            @Override
            protected void convert(ViewHolder holder, final TaskBean taskBean, int position) {
                holder.setVisible(R.id.mytask_evaluation, false); // 隐藏选标按钮
                // 农场主：1竞标中、2作业中、3已结束
                // 农机主：1已投标、2作业中、3已结束
                switch (taskBean.curstatu) {
                    case 1: // 竞标中
                        if (regType == 1) { // 农场主
                            holder.setText(R.id.mytask_status, "竞标中");
                            holder.setVisible(R.id.mytask_evaluation, true); // 显示“选标”按钮
                        } else if (regType == 2) {
                            holder.setText(R.id.mytask_status, "已投标");
                        }
                        break;

                    case 2: // 作业中
                        holder.setText(R.id.mytask_status, "作业中...");
                        if (regType == 2) {
                            holder.setVisible(R.id.mytask_evaluation, true); // 显示“完成项目”按钮
                            holder.setText(R.id.mytask_evaluation, "完成项目");
                        }
                        break;

                    case 3: // 已结束
                        holder.setText(R.id.mytask_status, "已结束");
//                        if (regType == 2) { // 农机手
                        holder.setVisible(R.id.mytask_evaluation, true); // 显示“评价”按钮

                        holder.setText(R.id.mytask_evaluation, TextUtils.isEmpty(taskBean.comment) ? "待评价" : "已评价");
//                        }
                        break;

                    case 4: // 已流标
                        holder.setText(R.id.mytask_status, "等待接单");
                        if (regType == 2) { // 农机手
                            holder.setVisible(R.id.mytask_evaluation, true); // 显示“接下项目”按钮
                            holder.setText(R.id.mytask_evaluation, "接下项目");
                        }
                        break;

                    case 5: // 招标成功
                        holder.setText(R.id.mytask_status, "招标成功");
                        break;
                }
                // 参与投标的人数
                holder.setText(R.id.mytask_join_num, "已有" + taskBean.participationnum + "人参与投标");
                // 主图片
                imageLoader.displayImage(Urls.BASE_IMGURL + taskBean.picfilepath, (ImageView) holder.getView(R.id.mytask_img));
                holder.setText(R.id.mytask_title, taskBean.name);
                holder.setText(R.id.mytask_crops, String.valueOf(Html.fromHtml(taskBean.content)));
                holder.setText(R.id.mytask_area, "作业面积：" + taskBean.operatingarea + "亩");
                holder.setText(R.id.mytask_limitedPrice, Html.fromHtml("竞标最高限价：<font color='#DF3F41'>" +
                        taskBean.limitedprice + "元</font>/亩") + "");
                holder.setText(R.id.mytask_totalPrice, Html.fromHtml("总价：<font color='#DF3F41'>" +
                        taskBean.totalprice + "</font>元") + "");
                holder.setText(R.id.mytask_schedule, "预计工期：" + taskBean.timelimit + "天");
                holder.setText(R.id.mytask_startWorkTime, "开工时间：" + DateUtil.getTime(taskBean.starttime + "", "yyyy-MM-dd"));
                holder.setText(R.id.mytask_undertakeType, taskBean.needstar + "星以上用户可接");

                // 选标
                holder.getView(R.id.mytask_evaluation).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (regType == 1) { // 农场主，当前为“选标”按钮
                            if (taskBean.curstatu == 3) { // 已结束
                                // 评价
                                Bundle bundle = new Bundle();
                                bundle.putInt("taskid", taskBean.id);
                                startActivity(EvaluationActivity.class, bundle);
                            } else {
                                if (taskBean.participationnum < 1) {
                                    ToastUtil.showShort(mContext, "暂时无人投标，请浏览其它任务");
                                } else {
                                    Bundle bundle = new Bundle();
                                    bundle.putInt("taskid", taskBean.id);
                                    startActivity(BidSelectionActivity.class, bundle);
                                }
                            }
                        } else if (regType == 2) { // 农机主，当前为“接下项目（4）”和“评价（3）”和“完成项目（2）”
                            if (taskBean.curstatu == 2) { // 完成项目
                                finishProject(taskBean.id, taskBean);
                            } else if (taskBean.curstatu == 3) { // 已结束
                                // 评价
                                Bundle bundle = new Bundle();
                                bundle.putInt("taskid", taskBean.id);
                                startActivity(EvaluationActivity.class, bundle);
                            } else if (taskBean.curstatu == 4) { // 等待接单
                                // 接下项目
                                acceptTask(taskBean.id, taskBean);
                            }
                        }
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
                            getTaskList(false);
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

                Bundle bundle = new Bundle();
                bundle.putSerializable("TaskBean", taskList.get(position));
                Intent intent = new Intent(MyReleaseTaskActivity.this, TaskCenterDetailsActivity.class);
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
                        if (taskList.size() != 0) {
                            taskList.clear();
                        }
                        start = 0;
                        getTaskList(true);
                        mSwipeRefresh.setRefreshing(false);
                    }
                }, 2000);
            }
        });
    }

    /**
     * 完成项目 id -- 任务的id
     *
     * @param id
     */
    private void finishProject(final int id, final TaskBean taskBean) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("本操作不可恢复，确认操作？");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishTask(id, taskBean);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create();
        builder.show();
    }

    private void finishTask(int id, final TaskBean taskBean) {
        dialog.show();
        // 拼接参数
        OkGo.post(Urls.URL_OWNER_FINISH_TASK)
                .tag(this)
                .params("taskid", id)
                .execute(new JsonCallback<ServerResult>() {
                    @Override
                    public void onSuccess(ServerResult serverResult, Call call, Response response) {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        ToastUtil.showShort(MyReleaseTaskActivity.this, serverResult.msg);
                        taskBean.curstatu = 3;
                        mLoadMoreWrapper.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.showShort(mContext, "网络错误");
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });
    }

    /**
     * 接下项目
     */
    private void acceptTask(int taskid, final TaskBean taskBean) {
        dialog.show();
        // 拼接参数
        OkGo.post(Urls.URL_OWNER_ACCEPT_TASK)
                .tag(this)
                .params("uid", (int) SharedPreferencesUtil.get(mContext, Consts.USER_UID, 0))
                .params("taskid", taskid)
                .execute(new JsonCallback<ServerResult>() {
                    @Override
                    public void onSuccess(ServerResult serverResult, Call call, Response response) {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        ToastUtil.showShort(MyReleaseTaskActivity.this, serverResult.msg);
                        taskBean.curstatu = 2;
                        mLoadMoreWrapper.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.showShort(mContext, "网络错误");
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });
    }

    private void getTaskList(boolean isShow) {
        String url = "";
        if (isShow) {
            dialog.show();
        }
        if (regType == 1) { // 农场主，我发布的任务
            url = Urls.URL_MYRELEASE_TASK;
        } else if (regType == 2) { // 农机主，我参与的任务
            url = Urls.URL_OWNER_JOIN_TASK;
        }
        // 拼接参数
        OkGo.post(url)
                .tag(this)
                .params("uid", (int) SharedPreferencesUtil.get(mContext, Consts.USER_UID, 0))
                .params("start", start)
                .params("perpage", perpage)
                .execute(new JsonCallback<List<TaskBean>>() {
                    @Override
                    public void onSuccess(List<TaskBean> taskBeanList, Call call, Response response) {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        isLoading = taskBeanList.size();
                        if (isLoading > 0) {
                            taskList.addAll(taskBeanList);
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

    @OnClick({R.id.id_title_left, R.id.id_title_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_title_left: // 返回
                finish();
                break;

            case R.id.id_title_right:
                if (regType == 1) {
                    anim.start();
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getTaskList(false);
    }
}
