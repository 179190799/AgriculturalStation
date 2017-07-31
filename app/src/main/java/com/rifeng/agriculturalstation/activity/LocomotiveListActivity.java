package com.rifeng.agriculturalstation.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.rifeng.agriculturalstation.BaseActivity;
import com.rifeng.agriculturalstation.R;
import com.rifeng.agriculturalstation.bean.OwnerList;
import com.rifeng.agriculturalstation.callback.JsonCallback;
import com.rifeng.agriculturalstation.recyclerview.CommonAdapter;
import com.rifeng.agriculturalstation.recyclerview.DividerItemDecoration;
import com.rifeng.agriculturalstation.recyclerview.base.ViewHolder;
import com.rifeng.agriculturalstation.recyclerview.wrapper.HeaderAndFooterWrapper;
import com.rifeng.agriculturalstation.recyclerview.wrapper.LoadMoreWrapper;
import com.rifeng.agriculturalstation.utils.CityJson;
import com.rifeng.agriculturalstation.utils.Consts;
import com.rifeng.agriculturalstation.utils.CustomProgressDialog;
import com.rifeng.agriculturalstation.utils.SharedPreferencesUtil;
import com.rifeng.agriculturalstation.utils.ToastUtil;
import com.rifeng.agriculturalstation.utils.Urls;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 农机手列表
 * <p/>
 * Created by chw on 2016/11/2.
 */
public class LocomotiveListActivity extends BaseActivity {

    @BindView(R.id.id_title_left)
    TextView idTitleLeft;
    @BindView(R.id.id_title_middle)
    TextView idTitleMiddle;
    @BindView(R.id.id_title_right)
    TextView idTitleRight; // 添加农机
    @BindView(R.id.locomotive_province)
    Spinner locomotiveProvince;
    @BindView(R.id.locomotive_city)
    Spinner locomotiveCity;
    @BindView(R.id.loco_recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R.id.loco_swiperefresh)
    SwipeRefreshLayout mSwipeRefresh;

    private ImageLoader imageLoader = ImageLoader.getInstance();
    private CityJson cityJson;
    private List<String> mCityList;
    private ArrayList<OwnerList> locomotiveList = new ArrayList<>();
    private CommonAdapter<OwnerList> mAdapter;
    private HeaderAndFooterWrapper mHeaderAndFooterWrapper;
    private LoadMoreWrapper mLoadMoreWrapper;

    private String type; // 我的农机信息mine，所有的农机信息all
    private CustomProgressDialog dialog;
    private ObjectAnimator anim; // 属性动画
    private String selectedProvince = "";
    private String selectedCity = "";
    private int start = 0;
    private int perpage = 8;
    private int isLoading = 0;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_locomotivelist;
    }

    @Override
    protected void initData() {
        type = this.getIntent().getStringExtra("type");
        if (type.equals("all")) {
            idTitleMiddle.setText("农机手");
        } else if (type.equals("mine")) {
            idTitleMiddle.setText("我的农机");
        }
        dialog = new CustomProgressDialog(mContext, "拼命加载中...");
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
                if ((int) SharedPreferencesUtil.get(LocomotiveListActivity.this, Consts.USER_UID, 0) > 0) {
                    if ((int) SharedPreferencesUtil.get(LocomotiveListActivity.this, Consts.USER_REGTYPE, 0) == 2) {
                        startActivity(AddLocomotiveActivity.class); // 添加农机
                    } else {
                        ToastUtil.showShort(LocomotiveListActivity.this, "您不是农机主哦");
                    }
                } else {
                    ToastUtil.showShort(LocomotiveListActivity.this, "请先登录");
                }
            }
        });

        cityJson = new CityJson(this);
        areaSpinner();
        getOwnerList(true, "");
        initialize();
    }

    private void areaSpinner() {
        ArrayAdapter<String> proAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cityJson.getmProvinceDatas());
        proAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locomotiveProvince.setAdapter(proAdapter);
        locomotiveProvince.setSelection(0, false);
        updateCity("北京");
        locomotiveProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String pro = parent.getSelectedItem().toString();
                // 根据省份更新城市数据
                updateCity(pro);
                selectedProvince = pro;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        locomotiveCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCity = parent.getSelectedItem().toString();
                if (locomotiveList.size() != 0) {
                    locomotiveList.clear();
                }
                start = 0;
                getOwnerList(true, "area");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * 根据省份更新城市数据
     *
     * @param pro
     */
    private void updateCity(String pro) {
        mCityList = Arrays.asList(cityJson.getmCitisDatasMap().get(pro));
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mCityList);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locomotiveCity.setAdapter(cityAdapter);
        locomotiveCity.setSelection(0, false);
        cityAdapter.notifyDataSetChanged();
    }

    private void initialize() {
        mSwipeRefresh.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // 添加分隔线
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        mAdapter = new CommonAdapter<OwnerList>(this, R.layout.item_locomotivelist, locomotiveList) {

            @Override
            protected void convert(ViewHolder holder, OwnerList ownerList, int position) {
                switch (ownerList.naturework) {
                    case 1:
                        holder.setText(R.id.loco_item_nature, "工作性质：收割");
                        break;

                    case 2:
                        holder.setText(R.id.loco_item_nature, "工作性质：灌溉");
                        break;

                    case 3:
                        holder.setText(R.id.loco_item_nature, "工作性质：耕作");
                        break;

                    case 4:
                        holder.setText(R.id.loco_item_nature, "工作性质：运输");
                        break;
                }
//                if (!TextUtils.isEmpty(ownerList.avatar)) {
                    imageLoader.displayImage(Urls.BASE_IMGURL + ownerList.avatar,
                            (ImageView) holder.getView(R.id.loco_item_img));
//                }
                holder.setText(R.id.loco_item_name, "农机手：" + ownerList.locomaster);
                holder.setText(R.id.loco_item_owner, "机车名称：" + ownerList.locomotive);
                holder.setText(R.id.loco_item_time, "运营时间：" + ownerList.operatingtime);
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
                        if (!selectedCity.equals("")) {
                            getOwnerList(false, "area");
                        } else {
                            if (isLoading > 0) {
                                getOwnerList(false, "");
                            }
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
                bundle.putString("username", locomotiveList.get(position).username);
                bundle.putInt("uid", locomotiveList.get(position).uid);
                startActivity(LocomotiveDetailsActivity.class, bundle);
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
                        if (locomotiveList.size() != 0) {
                            locomotiveList.clear();
                        }
                        start = 0;
                        getOwnerList(true, "");
                        mSwipeRefresh.setRefreshing(false);
                    }
                }, 2000);
            }
        });
    }

    private void getOwnerList(boolean isShow, String areaStr) {
        if (isShow) {
            dialog.show();
        }
        HttpParams params = new HttpParams();
        params.put("start", start);
        params.put("perpage", perpage);
        if (areaStr.equals("area")) {
            params.put("provinces", selectedProvince);
            params.put("city", selectedCity);
        }

        // 拼接参数
        OkGo.post(Urls.URL_ALL_LOCOMOTIVE_LIST)
                .tag(this)
                .params(params)
                .execute(new JsonCallback<List<OwnerList>>() {
                    @Override
                    public void onSuccess(List<OwnerList> ownerLists, Call call, Response response) {
                        if(dialog != null && dialog.isShowing()){
                            dialog.dismiss();
                        }
                        isLoading = ownerLists.size();
                        if(isLoading > 0){
                            locomotiveList.addAll(ownerLists);
                            start += isLoading;
                        }
                        if (isLoading < perpage) {
                            mLoadMoreWrapper.setLoadMoreView(0);
                        }else {
                            mLoadMoreWrapper.setLoadMoreView(R.layout.default_loading);
                        }
                        mLoadMoreWrapper.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.showShort(mContext, "加载失败");
                        if(dialog != null && dialog.isShowing()){
                            dialog.dismiss();
                        }
                    }
                });
    }

    @OnClick({R.id.id_title_left, R.id.id_title_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_title_left: // 返回
                finish();
                break;

            case R.id.id_title_right:
                anim.start();
                break;
        }
    }
}
