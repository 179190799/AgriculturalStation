package com.rifeng.agriculturalstation.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.rifeng.agriculturalstation.BaseActivity;
import com.rifeng.agriculturalstation.R;
import com.rifeng.agriculturalstation.bean.FarmBean;
import com.rifeng.agriculturalstation.bean.ServerResult;
import com.rifeng.agriculturalstation.callback.JsonCallback;
import com.rifeng.agriculturalstation.recyclerview.CommonAdapter;
import com.rifeng.agriculturalstation.recyclerview.base.ViewHolder;
import com.rifeng.agriculturalstation.recyclerview.wrapper.HeaderAndFooterWrapper;
import com.rifeng.agriculturalstation.recyclerview.wrapper.LoadMoreWrapper;
import com.rifeng.agriculturalstation.utils.Consts;
import com.rifeng.agriculturalstation.utils.CustomProgressDialog;
import com.rifeng.agriculturalstation.utils.SharedPreferencesUtil;
import com.rifeng.agriculturalstation.utils.ToastUtil;
import com.rifeng.agriculturalstation.utils.Urls;
import com.rifeng.agriculturalstation.utils.ViewPagerImageLoader;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;


/**
 * 农场详情
 * <p/>
 * Created by chw on 2016/10/31.
 */
public class FarmDetailsActivity extends BaseActivity {

    @BindView(R.id.id_title_left)
    TextView idTitleLeft;
    @BindView(R.id.id_title_middle)
    TextView idTitleMiddle;
    @BindView(R.id.id_title_right)
    TextView idTitleRight; // 添加农场
    @BindView(R.id.id_recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R.id.id_swiperefresh)
    SwipeRefreshLayout mSwipeRefresh;

    private ArrayList<FarmBean> farmList = null;
    private CommonAdapter<FarmBean> mAdapter;
    private HeaderAndFooterWrapper mHeaderAndFooterWrapper;
    private LoadMoreWrapper mLoadMoreWrapper;

    private View headerView;
    private TextView usernameTV;
    private TextView evaluationTV;
    private CustomProgressDialog dialog;
    private ObjectAnimator anim; // 属性动画
    private String farmerUserName; // 用户名
    private int farmerUid; // uid
    private int start = 0;
    private int perpage = 2;
    private int isLoading = 0;
    private int regType;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_farmdetails;
    }

    @Override
    protected void initData() {
        farmList = new ArrayList<>();
        dialog = new CustomProgressDialog(this, "拼命加载中...");
        regType = (int) SharedPreferencesUtil.get(mContext, Consts.USER_REGTYPE, 0);
        idTitleMiddle.setText("农场主");
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
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (regType == 1) {
                    startActivity(AddFarmActivity.class); // 添加农场
                } else {
                    ToastUtil.showShort(mContext,"您不是农场主哦");
                }
            }
        });

        Intent intent = getIntent();
        farmerUserName = intent.getStringExtra("username");
        farmerUid = intent.getIntExtra("uid", 0);

        initHeaderView();
        // 获取最新评论
        getLatestComments();
        // 获取农场列表
        getFarmList(true);
        initialize();
    }

    private void getLatestComments() {
        // 拼接参数
        OkGo.post(Urls.URL_FARM_COMMENT)
                .tag(this)
                .params("uid", farmerUid)
                .params("type", "farmer")
                .execute(new JsonCallback<ServerResult>() {
                    @Override
                    public void onSuccess(ServerResult serverResult, Call call, Response response) {
                        evaluationTV.setText(serverResult.msg);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.showShort(mContext, "获取评论失败");
                    }
                });
    }

    private void initHeaderView() {
        headerView = View.inflate(this, R.layout.farm_owne_header, null);
        usernameTV = (TextView) headerView.findViewById(R.id.foh_username);
        evaluationTV = (TextView) headerView.findViewById(R.id.foh_content);

        usernameTV.setText("农场主：" + farmerUserName);
    }

    private void getFarmList(boolean isShow) {
        if (isShow) {
            dialog.show();
        }
        // 拼接参数
        OkGo.post(Urls.URL_FARM_DETAILS)
                .tag(this)
                .params("uid", farmerUid)
                .params("start", start)
                .params("perpage", perpage)
                .execute(new JsonCallback<List<FarmBean>>() {
                    @Override
                    public void onSuccess(List<FarmBean> farmBeanList, Call call, Response response) {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        if (farmBeanList != null) {
                            isLoading = farmBeanList.size();
                            if (isLoading > 0) {
                                farmList.addAll(farmBeanList);
                                start += isLoading;
                            }
                            if (isLoading < perpage) {
                                mLoadMoreWrapper.setLoadMoreView(0);
                            } else {
                                mLoadMoreWrapper.setLoadMoreView(R.layout.default_loading);
                            }
                            mLoadMoreWrapper.notifyDataSetChanged();
                        }
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

    private void initialize() {
        mSwipeRefresh.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new CommonAdapter<FarmBean>(this, R.layout.item_farmerdetails, farmList) {

            @Override
            protected void convert(ViewHolder holder, final FarmBean farmBean, int position) {
                // 农场名称
                holder.setText(R.id.farm_details_name, "农场名称：" + farmBean.name);
                // 农场面积
                holder.setText(R.id.farm_details_area, "农场面积：" + farmBean.floorspace + "亩");
                // 主要作物
                holder.setText(R.id.farm_details_crops, "主要农作物：" + farmBean.mainproduct);
                // 农场所在地
                holder.setText(R.id.farm_details_address, "农场所在地：" + farmBean.farmaddress);
                // 相关图片
                if (farmBean.picarr != null && farmBean.picarr.size() > 0) {
                    Banner banner = holder.getView(R.id.banner);
                    banner.setImages(farmBean.picarr).setImageLoader(new ViewPagerImageLoader()).start();
                    banner.setOnBannerListener(new OnBannerListener() {
                        @Override
                        public void OnBannerClick(int position) {
                            Bundle bundle = new Bundle();
                            bundle.putString("flag", "multiple");
                            bundle.putStringArrayList("select", (ArrayList<String>) farmBean.picarr);
                            startActivity(PhotoViewActivity.class, bundle);
                        }
                    });
                }
            }
        };

        mHeaderAndFooterWrapper = new HeaderAndFooterWrapper(mAdapter);
        mLoadMoreWrapper = new LoadMoreWrapper(mHeaderAndFooterWrapper);

        mHeaderAndFooterWrapper.addHeaderView(headerView);

        mLoadMoreWrapper.setLoadMoreView(0);
        mLoadMoreWrapper.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isLoading > 0) {
                            getFarmList(false);
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
                        if (farmList.size() != 0) {
                            farmList.clear();
                        }
                        start = 0;
                        getFarmList(true);
                        mSwipeRefresh.setRefreshing(false);
                    }
                }, 2000);
            }
        });
    }


    @OnClick({R.id.id_title_left, R.id.id_title_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_title_left: // 返回
                finish();
                break;

            case R.id.id_title_right: // 添加农场
                anim.start();
                break;
        }
    }
}
