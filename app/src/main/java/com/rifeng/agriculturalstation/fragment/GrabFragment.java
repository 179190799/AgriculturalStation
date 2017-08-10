package com.rifeng.agriculturalstation.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.rifeng.agriculturalstation.BaseFragment;
import com.rifeng.agriculturalstation.R;
import com.rifeng.agriculturalstation.activity.ReleaseTaskActivity;
import com.rifeng.agriculturalstation.activity.SearchActivity;
import com.rifeng.agriculturalstation.activity.TaskCenterDetailsActivity;
import com.rifeng.agriculturalstation.bean.PicturesBean;
import com.rifeng.agriculturalstation.bean.TaskBean;
import com.rifeng.agriculturalstation.callback.JsonCallback;
import com.rifeng.agriculturalstation.recyclerview.CommonAdapter;
import com.rifeng.agriculturalstation.recyclerview.DividerItemDecoration;
import com.rifeng.agriculturalstation.recyclerview.base.ViewHolder;
import com.rifeng.agriculturalstation.recyclerview.wrapper.HeaderAndFooterWrapper;
import com.rifeng.agriculturalstation.recyclerview.wrapper.LoadMoreWrapper;
import com.rifeng.agriculturalstation.utils.AsyncHttpUtil;
import com.rifeng.agriculturalstation.utils.BroadCastManager;
import com.rifeng.agriculturalstation.utils.CityJson;
import com.rifeng.agriculturalstation.utils.Consts;
import com.rifeng.agriculturalstation.utils.CustomProgressDialog;
import com.rifeng.agriculturalstation.utils.DateUtil;
import com.rifeng.agriculturalstation.utils.LogUtil;
import com.rifeng.agriculturalstation.utils.SharedPreferencesUtil;
import com.rifeng.agriculturalstation.utils.ToastUtil;
import com.rifeng.agriculturalstation.utils.Urls;

import org.apache.http.Header;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 抢单
 * <p>
 * Created by chw on 2016/10/18.
 */
public class GrabFragment extends BaseFragment {

    @BindView(R.id.id_title_left)
    TextView idTitleLeft;
    @BindView(R.id.id_title_middle)
    TextView idTitleMiddle;
    @BindView(R.id.top_bg)
    ImageView topBg;
    @BindView(R.id.home_current_location)
    TextView home_current_location;// 当前位置
    @BindView(R.id.home_forum)
    TextView home_forum; // 论坛
    @BindView(R.id.home_search)
    TextView homeSearch;
    @BindView(R.id.home_relese)
    TextView homeRelese;
    @BindView(R.id.province_spinner)
    Spinner province; // 省
    @BindView(R.id.city_spinner)
    Spinner city; // 市
    @BindView(R.id.taskfrag_time)
    TextView timeCondition;// 时间
    @BindView(R.id.taskfrag_price)
    TextView priceCondition;// 项目款
    @BindView(R.id.task_center_recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R.id.task_center_swiperefresh)
    SwipeRefreshLayout mSwipeRefresh;

    private ImageLoader imageLoader = ImageLoader.getInstance();
    private CityJson cityJson;
    private List<String> mCityList;
    private ArrayList<TaskBean> taskList = new ArrayList<>();
    private CommonAdapter<TaskBean> mAdapter;
    private HeaderAndFooterWrapper mHeaderAndFooterWrapper;
    private LoadMoreWrapper mLoadMoreWrapper;

    private boolean timeFlag = true; // 用于区分时间箭头是向上还是向下，true向上、false向下
    private boolean priceFlag = true; // 用于区分项目款箭头是向上还是向下，true向上、false向下
    private CustomProgressDialog dialog;
    private String selectedProvince = "";
    private String selectedCity = "";
    private int start = 0;
    private int perpage = 4;
    private int isLoading = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // register
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // unregister
        EventBus.getDefault().unregister(this);
    }

    /**
     * 接收到事件后更新详情
     *
     * @param str
     */
    @Subscribe
    public void onEventMainThread(String str) {
        if (str != null) {
            home_current_location.setText("您当前的位置：" + str);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_task;
    }

    @Override
    protected void initData() {
        dialog = new CustomProgressDialog(getContext(), "正在加载...");
        idTitleLeft.setCompoundDrawables(null, null, null, null);
        idTitleMiddle.setText("任务中心");

        cityJson = new CityJson(getActivity());
        areaSpinner();
        initialize();
    }

    /**
     * 当Fragment对用户可见（即用户切换到此Fragment时）执行此方法
     */
    @Override
    protected void lazyLoad() {
        if (!isVisible) {
            return;
        }else {
            LogUtil.i("TAG", "GrabFragment --- UI布局可见");
            // 从服务器获取数据
            getTaskList(false, "");
        }
    }

    private void areaSpinner() {
        ArrayAdapter<String> proAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, cityJson.getmProvinceDatas());
        proAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        province.setAdapter(proAdapter);
        province.setSelection(0, false);
        updateCity("北京");
    }

    @OnItemSelected(value = {R.id.province_spinner, R.id.city_spinner}, callback = OnItemSelected.Callback.ITEM_SELECTED)
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(isVisible){
            if(parent.getId() == R.id.province_spinner){
                String pro = parent.getSelectedItem().toString();
                // 根据省份更新城市数据
                updateCity(pro);
                selectedProvince = pro;
            }else if(parent.getId() == R.id.city_spinner){
                selectedCity = parent.getSelectedItem().toString();
                if (taskList.size() != 0) {
                    taskList.clear();
                }
                start = 0;
                getTaskList(true, "area");
            }
        }
    }

    /**
     * 根据省份更新城市数据
     *
     * @param pro
     */
    private void updateCity(String pro) {
        mCityList = Arrays.asList(cityJson.getmCitisDatasMap().get(pro));
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, mCityList);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        city.setAdapter(cityAdapter);
        city.setSelection(0, false);
        cityAdapter.notifyDataSetChanged();
    }

    private void initialize() {
        mSwipeRefresh.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // 添加分隔线
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        mAdapter = new CommonAdapter<TaskBean>(getActivity(), R.layout.item_taskcenter, taskList) {

            @Override
            protected void convert(ViewHolder holder, TaskBean taskBean, int position) {
                imageLoader.displayImage(Urls.BASE_IMGURL + taskBean.picfilepath, (ImageView) holder.getView(R.id.taskcenter_item_img));
                holder.setText(R.id.taskcenter_item_title, taskBean.name);
                holder.setText(R.id.taskcenter_item_crops, String.valueOf(Html.fromHtml(taskBean.content)));
                holder.setText(R.id.taskcenter_item_area, "作业面积：" + taskBean.operatingarea + "亩");
                holder.setText(R.id.taskcenter_item_totalPrice, "项目款：￥" + taskBean.totalprice);
                holder.setText(R.id.taskcenter_item_undertakeType, taskBean.meetuser);
                holder.setText(R.id.taskcenter_item_endtime, "竞标截止日期：" + DateUtil.getTime(taskBean.enddate + "", "yyyy-MM-dd"));
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
                            getTaskList(false, "area");
                        } else {
                            if (isLoading > 0) {
                                getTaskList(false, "");
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
                Intent intent = new Intent(getActivity(), TaskCenterDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("TaskBean", taskList.get(position));
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
                        timeFlag = priceFlag = true;
                        changTimeArrow();
                        changPriceArrow();
                        if (taskList.size() != 0) {
                            taskList.clear();
                        }
                        start = 0;
                        getTaskList(true, "");
                        mSwipeRefresh.setRefreshing(false);
                    }
                }, 2000);
            }
        });
    }

    private void getTaskList(boolean isShow, String areaStr) {
        if (isShow) {
            dialog.show();
        }
        HttpParams params = new HttpParams();
        if (!timeFlag) { // 时间箭头向下，用户选择了时间查询条件，高--（向上）-->低--（向下）-->高
            params.put("time", "desc");
        }
        if (!priceFlag) { // 项目款箭头向下，用户选择了项目款查询条件
            params.put("price", "desc");
        }

        if (areaStr.equals("area")) {
            params.put("provinces", selectedProvince); // 省
            params.put("city", selectedCity); // 市
        }
        // 拼接参数
        OkGo.post(Urls.URL_TASK_LIST)
            .tag(this)
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
                    ToastUtil.showShort(getActivity(), "加载失败");
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            });
    }

    /**
     * 改变时间的箭头
     */
    private void changTimeArrow() {
        Drawable drawableLeft = null;
        if (timeFlag) { // 箭头向上
            drawableLeft = getActivity().getResources().getDrawable(R.mipmap.up_arrow);

        } else { // 箭头向下
            drawableLeft = getActivity().getResources().getDrawable(R.mipmap.price_down_arrow);
        }
        // 必须设置图片大小，否则不显示
        drawableLeft.setBounds(0, 0, drawableLeft.getMinimumWidth(), drawableLeft.getMinimumHeight());
        timeCondition.setCompoundDrawables(null, null, drawableLeft, null);
        // 更新数据
//                addDatas();
    }

    /**
     * 改变项目款的箭头
     */
    private void changPriceArrow() {
        Drawable drawableLeftPrice = null;
        if (priceFlag) { // 箭头向上
            drawableLeftPrice = getActivity().getResources().getDrawable(R.mipmap.up_arrow);
        } else { // 箭头向下
            drawableLeftPrice = getActivity().getResources().getDrawable(R.mipmap.price_down_arrow);
        }
        // 必须设置图片大小，否则不显示
        drawableLeftPrice.setBounds(0, 0, drawableLeftPrice.getMinimumWidth(), drawableLeftPrice.getMinimumHeight());
        priceCondition.setCompoundDrawables(null, null, drawableLeftPrice, null);
        // 更新数据
//                addDatas();
    }

    @OnClick({R.id.home_forum, R.id.home_search, R.id.home_relese, R.id.taskfrag_time, R.id.taskfrag_price})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.taskfrag_time: // 时间
                mLoadMoreWrapper.setLoadMoreView(R.layout.default_loading);
                timeFlag = !timeFlag; // 点击一次，标识取反
                changTimeArrow();

                if (!priceFlag) {
                    priceFlag = !timeFlag; // 点击一次，标识取反
                    changPriceArrow();
                }

                if (taskList.size() != 0) {
                    taskList.clear();
                }
                // 更新数据
                start = 0;
                getTaskList(true, "");
                break;

            case R.id.taskfrag_price: // 项目款
                mLoadMoreWrapper.setLoadMoreView(R.layout.default_loading);
                priceFlag = !priceFlag; // 点击一次，标识取反
                changPriceArrow();

                if (!timeFlag) {
                    timeFlag = !priceFlag; // 点击一次，标识取反
                    changTimeArrow();
                }

                if (taskList.size() != 0) {
                    taskList.clear();
                }
                // 更新数据
                start = 0;
                getTaskList(true, "");
                break;

            case R.id.home_forum:
                // 发送广播
                Intent forumIntent = new Intent();
                forumIntent.putExtra("type", "forum");
                forumIntent.setAction("fragment_forum");
                BroadCastManager.getInstance().sendBroadCast(getActivity(), forumIntent);
                break;

            case R.id.home_relese: // 发需求
                if ((int) SharedPreferencesUtil.get(getContext(), Consts.USER_REGTYPE, 0) == 1) {
                    startActivity(new Intent(getActivity(), ReleaseTaskActivity.class));
                } else {
                    ToastUtil.showShort(getContext(), "您不是农场主哦");
                }
                break;

            case R.id.home_search: // 搜索
                // 跳转到搜索界面
                startActivity(new Intent(getActivity(), SearchActivity.class));
                break;
        }
    }
}
