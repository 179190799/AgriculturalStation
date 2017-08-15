package com.rifeng.agriculturalstation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by chw on 2016/10/19.
 */
public abstract class BaseFragment extends Fragment {

    /**
     * Fragment当前状态是否可见
     */
    protected boolean isVisible;
    private Unbinder mBinder;

    // 抽象方法
    protected abstract int getLayoutId(); // 获取布局文件ID
    protected abstract void initData();

    // 初始化xml文件


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(getLayoutId() != 0){
            return inflater.inflate(getLayoutId(), null);
        }else {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

    // 注解绑定以及初始化组件


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinder = ButterKnife.bind(this, view);
        initData();
    }

    /**
     * 实现Fragment数据的缓加载
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    /**
     * 不可见
     */
    private void onInvisible() {

    }

    /**
     * 可见
     */
    private void onVisible() {
        lazyLoad();
    }

    /**
     * 延迟加载
     * 子类必须重写此方法
     *
     * 当Fragment对用户可见(即用户切换到此Fragment时)执行此方法
     */
    protected abstract void lazyLoad();

    // 最后解注册销毁等工作
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinder.unbind();
    }
}



































