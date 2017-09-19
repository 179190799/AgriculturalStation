package com.rifeng.agriculturalstation.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.rifeng.agriculturalstation.BaseFragment;
import com.rifeng.agriculturalstation.R;
import com.rifeng.agriculturalstation.activity.FarmDetailsActivity;
import com.rifeng.agriculturalstation.activity.LocomotiveDetailsActivity;
import com.rifeng.agriculturalstation.activity.LocomotiveListActivity;
import com.rifeng.agriculturalstation.activity.MyFarmListActivity;
import com.rifeng.agriculturalstation.activity.ReleaseTaskActivity;
import com.rifeng.agriculturalstation.activity.SearchActivity;
import com.rifeng.agriculturalstation.activity.TaskCenterDetailsActivity;
import com.rifeng.agriculturalstation.activity.TradeAlertsActivity;
import com.rifeng.agriculturalstation.bean.FarmerList;
import com.rifeng.agriculturalstation.bean.HomeBean;
import com.rifeng.agriculturalstation.bean.OwnerList;
import com.rifeng.agriculturalstation.bean.TaskBean;
import com.rifeng.agriculturalstation.callback.JsonCallback;
import com.rifeng.agriculturalstation.recyclerview.CommonAdapter;
import com.rifeng.agriculturalstation.recyclerview.DividerItemDecoration;
import com.rifeng.agriculturalstation.recyclerview.base.ViewHolder;
import com.rifeng.agriculturalstation.recyclerview.wrapper.HeaderAndFooterWrapper;
import com.rifeng.agriculturalstation.recyclerview.wrapper.LoadMoreWrapper;
import com.rifeng.agriculturalstation.utils.BroadCastManager;
import com.rifeng.agriculturalstation.utils.Consts;
import com.rifeng.agriculturalstation.utils.CustomProgressDialog;
import com.rifeng.agriculturalstation.utils.DateUtil;
import com.rifeng.agriculturalstation.utils.LogUtil;
import com.rifeng.agriculturalstation.utils.SharedPreferencesUtil;
import com.rifeng.agriculturalstation.utils.ToastUtil;
import com.rifeng.agriculturalstation.utils.Urls;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 主页
 * <p>
 * Created by chw on 2016/10/18.
 */
public class HomeFragment extends BaseFragment {


    @BindView(R.id.id_title_left)
    TextView idTitleLeft;
    @BindView(R.id.id_title_middle)
    TextView idTitleMiddle;
    @BindView(R.id.home_current_location)
    TextView homeCurrentLocation;
    @BindView(R.id.home_forum)
    TextView homeForum;
    @BindView(R.id.home_search)
    TextView homeSearch;
    @BindView(R.id.home_relese)
    TextView homeRelese;
    @BindView(R.id.home_farmer)
    TextView homeFarmer;
    @BindView(R.id.home_owner)
    TextView homeOwner;
    @BindView(R.id.home_taskcenter)
    TextView homeTaskcenter;
    @BindView(R.id.home_flash)
    TextView homeFlash;
    @BindView(R.id.home_task_recyclerview)
    RecyclerView taskRecyclerView;
    @BindView(R.id.home_task_more)
    TextView homeTaskMore;
    @BindView(R.id.home_farmer_recyclerview)
    RecyclerView farmerRecyclerView;
    @BindView(R.id.home_farmer_more)
    TextView homeFarmerMore;
    @BindView(R.id.home_owner_recyclerview)
    RecyclerView ownerRecyclerView;
    @BindView(R.id.home_owner_more)
    TextView homeOwnerMore;
    @BindView(R.id.home_search_edit)
    EditText searchText;

    private ImageLoader imageLoader = ImageLoader.getInstance();
    private CommonAdapter<TaskBean> mAdapter;
    private HeaderAndFooterWrapper mHeaderAndFooterWrapper;
    private LoadMoreWrapper mLoadMoreWrapper;

    private CommonAdapter<FarmerList> mFarmAdapter;
    private HeaderAndFooterWrapper mFarmHeaderAndFooterWrapper;
    private LoadMoreWrapper mFarmLoadMoreWrapper;

    private CommonAdapter<OwnerList> mLocoAdapter;
    private HeaderAndFooterWrapper mLocoHeaderAndFooterWrapper;
    private LoadMoreWrapper mLocoLoadMoreWrapper;

    private List<TaskBean> taskList = new ArrayList<>();
    private List<FarmerList> farmList = new ArrayList<>();
    private List<OwnerList> locomotiveList = new ArrayList<>();

    private CustomProgressDialog dialog;




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // register
//        EventBus.getDefault().register(this);
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        // unregister
        EventBus.getDefault().unregister(this);
    }

//    /**
//     * 接收到事件后更新详情
//     *
//     * @param str
//     */
//    @Subscribe
//    public void onEventMainThread(String str) {
//        if (str != null) {
//            homeCurrentLocation.setText("您当前的位置：" + str);
//        }
//    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initData() {
        dialog = new CustomProgressDialog(getActivity(), "正在搜索中...");
        idTitleLeft.setVisibility(View.GONE);
        idTitleMiddle.setText("首页");

        initialize();
    }

    /**
     * 当Fragment对用户可见（即用户切换到此Fragment时）执行此方法
     */
    @Override
    protected void lazyLoad() {
        if (!isVisible) {
            searchText.setText("");
            return;
        }else {
            LogUtil.i("TAG", "HomeFragment --- UI布局可见");
            // 获取数据
            getDatas();
        }
    }

    private void initialize() {
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new CommonAdapter<TaskBean>(getActivity(), R.layout.item_taskcenter, taskList) {

            @Override
            protected void convert(ViewHolder holder, TaskBean taskBean, int position) {
                imageLoader.displayImage(Urls.BASE_IMGURL + taskBean.picfilepath, (ImageView) holder.getView(R.id.taskcenter_item_img));
                holder.setText(R.id.taskcenter_item_title, taskBean.name);
                holder.setText(R.id.taskcenter_item_crops, String.valueOf(Html.fromHtml(taskBean.content)));
                holder.setText(R.id.taskcenter_item_area, "作业面积：" + taskBean.operatingarea + "亩");
                holder.setText(R.id.taskcenter_item_totalPrice, "项目款：￥" + taskBean.totalprice);
                holder.setText(R.id.taskcenter_item_undertakeType, "本地用户 和 "+taskBean.needstar + " 星以上用户可接");
                holder.setText(R.id.taskcenter_item_endtime, "竞标截止日期：" + DateUtil.getTime(taskBean.enddate + "", "yyyy-MM-dd"));
            }
        };

        mHeaderAndFooterWrapper = new HeaderAndFooterWrapper(mAdapter);
        mLoadMoreWrapper = new LoadMoreWrapper(mHeaderAndFooterWrapper);

        // 设置适配器数据
        taskRecyclerView.setAdapter(mLoadMoreWrapper);
        taskRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL_LIST));
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


        /******************************* 农场主 ******************************************************/
        farmerRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mFarmAdapter = new CommonAdapter<FarmerList>(getActivity(), R.layout.item_home_farmer, farmList) {

            @Override
            protected void convert(ViewHolder holder, FarmerList farmerList, int position) {
                imageLoader.displayImage(Urls.BASE_IMGURL + farmerList.avatar, (ImageView) holder.getView(R.id.home_item_farmer_img));
                holder.setText(R.id.home_item_farmer_title, farmerList.username);
                holder.setText(R.id.home_item_farmer_farmer, "农场主：" + farmerList.farmer);
            }
        };

        mFarmHeaderAndFooterWrapper = new HeaderAndFooterWrapper(mFarmAdapter);
        mFarmLoadMoreWrapper = new LoadMoreWrapper(mFarmHeaderAndFooterWrapper);

        // 设置适配器数据
        farmerRecyclerView.setAdapter(mFarmLoadMoreWrapper);
        // 设置item点击事件
        mFarmAdapter.setOnItemClickListener(new CommonAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {

                Intent intent = new Intent(getActivity(), FarmDetailsActivity.class);
                Bundle bundle = new Bundle(); // 存储数据
                bundle.putInt("uid", farmList.get(position).uid);
                bundle.putString("username", farmList.get(position).username);
                intent.putExtras(bundle);
                startActivity(intent);

                mFarmAdapter.notifyItemRemoved(position);
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });


        /*************************************** 农机手 *************************************/
        ownerRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mLocoAdapter = new CommonAdapter<OwnerList>(getActivity(), R.layout.item_home_farmer, locomotiveList) {

            @Override
            protected void convert(ViewHolder holder, OwnerList ownerList, int position) {
                imageLoader.displayImage(Urls.BASE_IMGURL + ownerList.avatar,
                            (ImageView) holder.getView(R.id.home_item_farmer_img));
                holder.setText(R.id.home_item_farmer_title, "农机手：" + ownerList.locomaster);
                holder.setText(R.id.home_item_farmer_farmer, "机车名称：" + ownerList.locomotive);
            }
        };

        mLocoHeaderAndFooterWrapper = new HeaderAndFooterWrapper(mLocoAdapter);
        mLocoLoadMoreWrapper = new LoadMoreWrapper(mLocoHeaderAndFooterWrapper);

        // 设置适配器数据
        ownerRecyclerView.setAdapter(mLocoLoadMoreWrapper);
        // 设置item点击事件
        mLocoAdapter.setOnItemClickListener(new CommonAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {

                Intent intent = new Intent(getActivity(), LocomotiveDetailsActivity.class);
                Bundle bundle = new Bundle(); // 存储数据
                bundle.putInt("uid", locomotiveList.get(position).uid);
                bundle.putString("username", locomotiveList.get(position).username);
                intent.putExtras(bundle);
                startActivity(intent);

                mLocoAdapter.notifyItemRemoved(position);
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
    }

    /**
     * 访问服务器获取数据
     */
    private void getDatas() {
        if(taskList.size() > 0){
            taskList.clear();
        }
        if(farmList.size() > 0){
            farmList.clear();
        }
        if(locomotiveList.size() > 0){
            locomotiveList.clear();
        }
        // 拼接参数
        OkGo.post(Urls.URL_HOME_INFO)
                .tag(this)
                .execute(new JsonCallback<HomeBean>() {
                    @Override
                    public void onSuccess(HomeBean homeBean, Call call, Response response) {
                        if(homeBean != null){
                            taskList.addAll(homeBean.task);
                            farmList.addAll(homeBean.farmer);
                            locomotiveList.addAll(homeBean.owner);
                        }
                        mLoadMoreWrapper.notifyDataSetChanged();
                        mFarmLoadMoreWrapper.notifyDataSetChanged();
                        mLocoLoadMoreWrapper.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.showShort(getActivity(), "加载失败");
                    }
                });
    }

    @OnClick({R.id.home_forum, R.id.home_search, R.id.home_relese,
            R.id.home_farmer, R.id.home_owner, R.id.home_taskcenter,
            R.id.home_flash, R.id.home_task_more, R.id.home_farmer_more,
            R.id.home_owner_more})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.home_forum: // 论坛
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

            case R.id.home_farmer: // 农场主
                Intent intent = new Intent(getActivity(), MyFarmListActivity.class);
                intent.putExtra("type", "all");
                startActivity(intent);
                break;

            case R.id.home_owner: // 农机手
                Intent intentLoco = new Intent(getActivity(), LocomotiveListActivity.class);
                intentLoco.putExtra("type", "all");
                startActivity(intentLoco);
                break;

            case R.id.home_taskcenter: // 任务中心
                // 发送广播
                Intent taskCenterIntent = new Intent();
                taskCenterIntent.putExtra("type", "taskMore");
                taskCenterIntent.setAction("fragment_taskMore");
                BroadCastManager.getInstance().sendBroadCast(getActivity(), taskCenterIntent);
                break;

            case R.id.home_flash: // 行业快讯
                startActivity(new Intent(getActivity(), TradeAlertsActivity.class));
                break;

            case R.id.home_task_more: // 任务中心-查看更多
                // 发送广播
                Intent taskMoreIntent = new Intent();
                taskMoreIntent.putExtra("type", "taskMore");
                taskMoreIntent.setAction("fragment_taskMore");
                BroadCastManager.getInstance().sendBroadCast(getActivity(), taskMoreIntent);
                break;

            case R.id.home_farmer_more: // 农场主-查看更多
                Intent intentMore = new Intent(getActivity(), MyFarmListActivity.class);
                intentMore.putExtra("type", "all");
                startActivity(intentMore);
                break;

            case R.id.home_owner_more: // 农机手-查看更多
                Intent locoIntent = new Intent(getActivity(), LocomotiveListActivity.class);
                locoIntent.putExtra("type", "all");
                startActivity(locoIntent);
                break;
            case R.id.home_search: // 搜索
                searchData(true);
                break;

        }
    }


    /**
     * 搜索数据
     */
    private void searchData(boolean isShow) {
        if (TextUtils.isEmpty(searchText.getText().toString().trim())) {
            ToastUtil.showShort(getActivity(), "请输入搜索内容");
            return;
        }
        if (isShow) {
            dialog.show();
        }
        // 拼接参数
        OkGo.post(Urls.URL_TASK_SEARCH)
                .tag(this)
                .params("start",0)
                .params("perpage",8)
                .params("searchname", searchText.getText().toString().trim())
                .execute(new JsonCallback<List<TaskBean>>() {
                    @Override
                    public void onSuccess(List<TaskBean> taskBeanList, Call call, Response response) {
                        dialog.dismiss();
                        if (taskList.size() > 0) {
                            taskList.clear();
                        }
                        if (taskBeanList.size() > 0) {
                            taskList.addAll(taskBeanList);
                            initialize();
                        }
                        mLoadMoreWrapper.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        dialog.dismiss();
                        ToastUtil.showShort(getActivity(), "查询失败");
                    }
                });
    }
}
