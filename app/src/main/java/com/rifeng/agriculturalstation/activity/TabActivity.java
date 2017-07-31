package com.rifeng.agriculturalstation.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.Poi;
import com.lzy.okgo.OkGo;
import com.rifeng.agriculturalstation.BaseApplication;
import com.rifeng.agriculturalstation.R;
import com.rifeng.agriculturalstation.bean.ServerResult;
import com.rifeng.agriculturalstation.callback.JsonCallback;
import com.rifeng.agriculturalstation.fragment.ForumFragment;
import com.rifeng.agriculturalstation.fragment.GrabFragment;
import com.rifeng.agriculturalstation.fragment.HomeFragment;
import com.rifeng.agriculturalstation.fragment.MineFragment;
import com.rifeng.agriculturalstation.fragment.TaskFragment;
import com.rifeng.agriculturalstation.service.LocationService;
import com.rifeng.agriculturalstation.utils.ActivityCollector;
import com.rifeng.agriculturalstation.utils.BroadCastManager;
import com.rifeng.agriculturalstation.utils.Consts;
import com.rifeng.agriculturalstation.utils.LogUtil;
import com.rifeng.agriculturalstation.utils.SharedPreferencesUtil;
import com.rifeng.agriculturalstation.utils.ToastUtil;
import com.rifeng.agriculturalstation.utils.Urls;
import com.rifeng.agriculturalstation.view.TabChangeView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by chw on 2016/10/17.
 *
 * EventBus定义：是一个发布 / 订阅的事件总线。
 *    - 其中一个功能：类似于广播的功能
 */
public class TabActivity extends FragmentActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private HomeFragment homeFragment; // 主页
    private TaskFragment taskFragment; // 任务
    private GrabFragment grabFragment; // 抢单
    private ForumFragment forumFragment; // 论坛
    private MineFragment mineFragment; // 我的
    private ViewPager mViewPager;
    private List<Fragment> mTabs = new ArrayList<>();
    private FragmentPagerAdapter mAdapter;
    private List<TabChangeView> mTabIndicators = new ArrayList<>();
    private LocalReceicer mReceiver;
    /**
     * 百度定位
     */
    private LocationService locationService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationService = ((BaseApplication) getApplication()).locationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(mListener);
        // 开启定位
        locationService.start(); // 定位SDK

        // register EventBus
        EventBus.getDefault().register(this);

        setContentView(R.layout.activity_tab);
        initView();
        initData();
        // 关闭预加载，默认一次只加载一个Fragment
//        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(mAdapter);
        initEvent();
    }

    /**
     * 如果这里不加上 @Subscribe 会报下面的异常
     *
     * 异常 EventBusException：its super classes have no public methods with the @Subscribe annotation
     *
     * @param str
     */
    @Subscribe
    public void onEventMainThread(String str){
        System.out.println("onEventMainThread = " + str);
    }

    /**
     * 初始化所有事件
     */
    private void initEvent() {
        mViewPager.setOnPageChangeListener(this);
    }

    private void initData() {

        homeFragment = new HomeFragment();
        mTabs.add(homeFragment);
        taskFragment = new TaskFragment();
        mTabs.add(taskFragment);
        grabFragment = new GrabFragment();
        mTabs.add(grabFragment);
        forumFragment = new ForumFragment();
        mTabs.add(forumFragment);
        mineFragment = new MineFragment();
        mTabs.add(mineFragment);

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mTabs.get(position);
            }

            @Override
            public int getCount() {
                return mTabs.size();
            }
        };
    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);

        TabChangeView tab_home = (TabChangeView) findViewById(R.id.id_tab_home);
        mTabIndicators.add(tab_home);
        TabChangeView tab_task = (TabChangeView) findViewById(R.id.id_tab_task);
        mTabIndicators.add(tab_task);
        TabChangeView tab_grab = (TabChangeView) findViewById(R.id.id_tab_grab);
        mTabIndicators.add(tab_grab);
        TabChangeView tab_forum = (TabChangeView) findViewById(R.id.id_tab_forum);
        mTabIndicators.add(tab_forum);
        TabChangeView tab_mine = (TabChangeView) findViewById(R.id.id_tab_mine);
        mTabIndicators.add(tab_mine);

        tab_home.setOnClickListener(this);
        tab_task.setOnClickListener(this);
        tab_grab.setOnClickListener(this);
        tab_forum.setOnClickListener(this);
        tab_mine.setOnClickListener(this);

        // 默认第一个选中
        tab_home.setIconAlpha(1.0f);

        // 接受广播
        IntentFilter filter = new IntentFilter();
        filter.addAction("fragment_forum");
        filter.addAction("fragment_taskMore");
        mReceiver = new LocalReceicer();
        // 注册广播接收者
        BroadCastManager.getInstance().registerReceiver(this, mReceiver, filter);
    }


    class LocalReceicer extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            // 收到广播后的处理
            String type = intent.getStringExtra("type");
            resetOtherTabs();
            if(type.equals("forum")){ // 论坛
                mTabIndicators.get(3).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(3, false);
            }else if(type.equals("taskMore")){ // 任务
                mTabIndicators.get(1).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(1, false);
            }
        }
    }

    @Override
    public void onClick(View v) {
        clickTab(v);

        // 点击其他按钮

    }

    /**
     * 点击Tab按钮
     *
     * @param v
     */
    private void clickTab(View v) {
        // 重置其他的TabIndicator的颜色
        resetOtherTabs();

        switch (v.getId()){
            case R.id.id_tab_home:
                mTabIndicators.get(0).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(0, false);
                break;

            case R.id.id_tab_task:
                mTabIndicators.get(1).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(1, false);
                break;

            case R.id.id_tab_grab:
                mTabIndicators.get(2).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(2, false);
                break;

            case R.id.id_tab_forum:
                mTabIndicators.get(3).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(3, false);
                break;

            case R.id.id_tab_mine:
                mTabIndicators.get(4).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(4, false);
                break;
        }
    }

    /**
     * 重置其他的TabIndicator的颜色
     */
    private void resetOtherTabs() {
        for(int i = 0; i < mTabIndicators.size(); i++){
            mTabIndicators.get(i).setIconAlpha(0);
        }
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        /**
         * 从第一页到第二页
         * position = 0; positionOffset 0.0 ~ 1.0
         * 从第二页到第一页
         * position = 0; positionOffset 1.0 ~ 0.0
         */
        if(positionOffset > 0){
            TabChangeView left = mTabIndicators.get(position);
            TabChangeView right = mTabIndicators.get(position + 1);
            left.setIconAlpha(1 - positionOffset);
            right.setIconAlpha(positionOffset);
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onDestroy() {
        BroadCastManager.getInstance().unregisterReceiver(this, mReceiver);
        // unregister EventBus
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /***
     * Stop location service
     */
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        locationService.unregisterListener(mListener); //注销掉监听
        locationService.stop(); //停止定位服务
        super.onStop();
    }

    /*****
     *
     * 定位结果回调，重写onReceiveLocation方法
     *
     */
    private BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                StringBuffer sb = new StringBuffer(256);
                sb.append("time : ");
                /**
                 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
                 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
                 */
                sb.append(location.getTime());
                sb.append("\nlocType : ");// 定位类型
                sb.append(location.getLocType());
                sb.append("\nlocType description : ");// *****对应的定位类型说明*****
                sb.append(location.getLocTypeDescription());
                sb.append("\nlatitude : ");// 纬度
                sb.append(location.getLatitude());
                sb.append("\nlontitude : ");// 经度
                sb.append(location.getLongitude());
                sb.append("\nradius : ");// 半径
                sb.append(location.getRadius());
                sb.append("\nCountryCode : ");// 国家码
                sb.append(location.getCountryCode());
                sb.append("\nCountry : ");// 国家名称
                sb.append(location.getCountry());
                sb.append("\ncitycode : ");// 城市编码
                sb.append(location.getCityCode());
                sb.append("\ncity : ");// 城市
                sb.append(location.getCity());
                sb.append("\nDistrict : ");// 区
                sb.append(location.getDistrict());
                sb.append("\nStreet : ");// 街道
                sb.append(location.getStreet());
                sb.append("\naddr : ");// 地址信息
                sb.append(location.getAddrStr());
                sb.append("\nUserIndoorState: ");// *****返回用户室内外判断结果*****
                sb.append(location.getUserIndoorState());
                sb.append("\nDirection(not all devices have value): ");
                sb.append(location.getDirection());// 方向
                sb.append("\nlocationdescribe: ");
                sb.append(location.getLocationDescribe());// 位置语义化信息
                sb.append("\nPoi: ");// POI信息
                if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
                    for (int i = 0; i < location.getPoiList().size(); i++) {
                        Poi poi = (Poi) location.getPoiList().get(i);
                        sb.append(poi.getName() + ";");
                    }
                }
                if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                    sb.append("\nspeed : ");
                    sb.append(location.getSpeed());// 速度 单位：km/h
                    sb.append("\nsatellite : ");
                    sb.append(location.getSatelliteNumber());// 卫星数目
                    sb.append("\nheight : ");
                    sb.append(location.getAltitude());// 海拔高度 单位：米
                    sb.append("\ngps status : ");
                    sb.append(location.getGpsAccuracyStatus());// *****gps质量判断*****
                    sb.append("\ndescribe : ");
                    sb.append("gps定位成功");
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                    // 运营商信息
                    if (location.hasAltitude()) {// *****如果有海拔高度*****
                        sb.append("\nheight : ");
                        sb.append(location.getAltitude());// 单位：米
                    }
                    sb.append("\noperationers : ");// 运营商信息
                    sb.append(location.getOperators());
                    sb.append("\ndescribe : ");
                    sb.append("网络定位成功");
                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                    sb.append("\ndescribe : ");
                    sb.append("离线定位成功，离线定位结果也是有效的");
                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    sb.append("\ndescribe : ");
                    sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    sb.append("\ndescribe : ");
                    sb.append("网络不同导致定位失败，请检查网络是否通畅");
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    sb.append("\ndescribe : ");
                    sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                }
//                logMsg(sb.toString());
                System.out.println(sb.toString());
                // 发布事件
                EventBus.getDefault().post(location.getCity());
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.i("TAG", "--TabActivity");
        ActivityCollector.finshAll();
    }
//
//    private long clickTime = 0;
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (KeyEvent.KEYCODE_BACK == keyCode) {
//            if ((System.currentTimeMillis() - clickTime) > 2000) {
//                ToastUtil.showShort(this, "再按一次退出程序");
//                clickTime = System.currentTimeMillis();
//            } else {
//                System.exit(0);
////                finish();
//            }
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
}
