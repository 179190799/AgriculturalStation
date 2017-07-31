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
import com.rifeng.agriculturalstation.bean.FarmerList;
import com.rifeng.agriculturalstation.callback.JsonCallback;
import com.rifeng.agriculturalstation.recyclerview.CommonAdapter;
import com.rifeng.agriculturalstation.recyclerview.DividerItemDecoration;
import com.rifeng.agriculturalstation.recyclerview.base.ViewHolder;
import com.rifeng.agriculturalstation.recyclerview.wrapper.HeaderAndFooterWrapper;
import com.rifeng.agriculturalstation.recyclerview.wrapper.LoadMoreWrapper;
import com.rifeng.agriculturalstation.utils.CityJson;
import com.rifeng.agriculturalstation.utils.Consts;
import com.rifeng.agriculturalstation.utils.CustomProgressDialog;
import com.rifeng.agriculturalstation.utils.LogUtil;
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
 * 个人信息--农场信息
 * <p>
 * Created by chw on 2016/10/31.
 */
public class MyFarmListActivity extends BaseActivity {

    @BindView(R.id.id_title_left)
    TextView idTitleLeft;
    @BindView(R.id.id_title_middle)
    TextView idTitleMiddle;
    @BindView(R.id.id_title_right)
    TextView idTitleRight; // 添加农场
    @BindView(R.id.farm_province)
    Spinner province;
    @BindView(R.id.farm_city)
    Spinner city;
    @BindView(R.id.myfarm_recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R.id.myfarm_swiperefresh)
    SwipeRefreshLayout mSwipeRefresh;

    private ImageLoader imageLoader = ImageLoader.getInstance();
    private CityJson cityJson;
    private List<String> mCityList;
    private ArrayList<FarmerList> farmList = null;
    private CommonAdapter<FarmerList> mAdapter;
    private HeaderAndFooterWrapper mHeaderAndFooterWrapper;
    private LoadMoreWrapper mLoadMoreWrapper;

    private ObjectAnimator anim; // 属性动画
    private String type; // 是所有的农场信息all，还是我的农场信息mine
    private CustomProgressDialog dialog;
    private String selectedProvince = "";
    private String selectedCity = "";
    private int start = 0;
    private int perpage = 8;
    private int isLoading = 0;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_myfarmlist;
    }

    @Override
    protected void initData() {
        farmList = new ArrayList<>();
        Intent intent = getIntent();
        type = intent.getStringExtra("type");

        if (type.equals("all")) {
            idTitleMiddle.setText("农场主");
        } else if (type.equals("mine")) {
            idTitleMiddle.setText("我的农场");
        }
        dialog = new CustomProgressDialog(this, "拼命加载中...");
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
                if ((int) SharedPreferencesUtil.get(MyFarmListActivity.this, Consts.USER_UID, 0) > 0) {
                    if ((int) SharedPreferencesUtil.get(MyFarmListActivity.this, Consts.USER_REGTYPE, 0) == 1) {
                        startActivity(AddFarmActivity.class); // 添加农场
                    } else {
                        ToastUtil.showShort(MyFarmListActivity.this, "您不是农场主哦");
                    }
                } else {
                    ToastUtil.showShort(MyFarmListActivity.this, "请先登录");
                }
            }
        });

        LogUtil.i("CHW", "---->" + Thread.currentThread().getId());

        cityJson = new CityJson(this);
        areaSpinner();
        getFarmerList(true, "");
        initialize();
    }

    private void areaSpinner() {
        ArrayAdapter<String> proAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cityJson.getmProvinceDatas());
        proAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        province.setAdapter(proAdapter);
        province.setSelection(0, false);
        updateCity("北京");
        province.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCity = parent.getSelectedItem().toString();
                if (farmList.size() != 0) {
                    farmList.clear();
                }
                start = 0;
                getFarmerList(true, "area");
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
        city.setAdapter(cityAdapter);
        city.setSelection(0, false);
        cityAdapter.notifyDataSetChanged();
    }

    private void initialize() {
        mSwipeRefresh.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // 添加分隔线
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        mAdapter = new CommonAdapter<FarmerList>(this, R.layout.item_myfarmlist, farmList) {

            @Override
            protected void convert(ViewHolder holder, FarmerList farmerList, int position) {
                // 用户头像
//                if (!TextUtils.isEmpty(farmerList.avatar)) {
                    imageLoader.displayImage(Urls.BASE_IMGURL + farmerList.avatar, (ImageView) holder.getView(R.id.myfarm_item_img));
//                }
                // 用户名
                holder.setText(R.id.myfarm_username, farmerList.username);
                // 农场主
                holder.setText(R.id.myfarm_farmer, "农场主：" + farmerList.farmer);
                // 农场面积
                holder.setText(R.id.myfarm_area, "农场面积：" + farmerList.floorspace + "亩");
                // 主要作物
                holder.setText(R.id.myfarm_crops, "主要农作物：" + farmerList.mainproduct);
                // 农场所在地
                holder.setText(R.id.myfarm_address, "农场所在地：" + farmerList.provinces + farmerList.city);
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
                            getFarmerList(false, "area");
                        } else {
                            if (isLoading > 0) {
                                getFarmerList(false, "");
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
                bundle.putString("username", farmList.get(position).username);
                bundle.putInt("uid", farmList.get(position).uid);
                startActivity(FarmDetailsActivity.class, bundle);

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
                        getFarmerList(true, "");
                        mSwipeRefresh.setRefreshing(false);
                    }
                }, 2000);
            }
        });
    }

    private void getFarmerList(boolean isShow, String areaStr){
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
        OkGo.post(Urls.URL_ALL_FARM_LIST)
                .tag(this)
                .params(params)
                .execute(new JsonCallback<List<FarmerList>>() {
                    @Override
                    public void onSuccess(List<FarmerList> farmerLists, Call call, Response response) {
                        LogUtil.i("CHW", "+++++--->" + Thread.currentThread().getId());
                        if(dialog != null && dialog.isShowing()){
                            dialog.dismiss();
                        }
                        if(farmerLists != null){
                            isLoading = farmerLists.size();
                            if(isLoading > 0){
                                farmList.addAll(farmerLists);
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
                        if(dialog != null && dialog.isShowing()){
                            dialog.dismiss();
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.cancel();
            }
            dialog = null;
        }
        super.onDestroy();
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
