package com.rifeng.agriculturalstation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.lzy.okgo.OkGo;
import com.rifeng.agriculturalstation.bean.ServerResult;
import com.rifeng.agriculturalstation.callback.JsonCallback;
import com.rifeng.agriculturalstation.utils.ActivityCollector;
import com.rifeng.agriculturalstation.utils.AsyncHttpUtil;
import com.rifeng.agriculturalstation.utils.Consts;
import com.rifeng.agriculturalstation.utils.LogUtil;
import com.rifeng.agriculturalstation.utils.SharedPreferencesUtil;
import com.rifeng.agriculturalstation.utils.ToastUtil;
import com.rifeng.agriculturalstation.utils.Urls;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Response;


/**
 * 对Activity类进行扩展
 *
 * Created by chw on 2016/10/17.
 */
public abstract class BaseActivity extends AppCompatActivity {

    /**
     * Log打印标签
     */
    protected String TAG = this.getClass().getSimpleName();
    /**
     * 全局的Context
     */
    protected Context mContext;
    private Unbinder mBinder;

    public BaseActivity(){}

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d("BaseActivity", TAG);
        this.mContext = this;
        if(this.getContentViewId() != 0){
            this.setContentView(getContentViewId());
            ActivityCollector.addActivity(this);
        }
        this.initData();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        mBinder = ButterKnife.bind(this);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        mBinder = ButterKnife.bind(this);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        mBinder = ButterKnife.bind(this);
    }

    protected abstract int getContentViewId();
    protected abstract void initData();

    protected final <T extends View> T getView(int id){
        try{
            return (T) this.findViewById(id);
        }catch (ClassCastException e){
            Log.e(this.TAG, "Could not cast View to onCreate class.", e);
            throw e;
        }
    }

    /**
     * 通过Class跳转界面
     *
     * @param cls
     */
    public void startActivity(Class<?> cls){
        startActivity(cls, null);
    }

    /**
     * 含有Bundle通过Class跳转界面
     *
     * @param cls
     * @param bundle
     */
    public void startActivity(Class<?> cls, Bundle bundle){
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if(bundle != null){
            intent.putExtras(bundle);
        }
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    /**
     * 通过Action跳转界面
     *
     * @param action
     */
    public void startActivity(String action){
        startActivity(action, null);
    }

    /**
     * 含有Bundle通过Action跳转界面
     *
     * @param action
     * @param bundle
     */
    public void startActivity(String action, Bundle bundle){
        Intent intent = new Intent();
        intent.setAction(action);
        if(bundle != null){
            intent.putExtras(bundle);
        }
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    /**
     * 含有Bundle通过Class打开编辑界面
     *
     * @param cls
     * @param bundle
     * @param requestCode
     */
    public void startActivityForResult(Class<?> cls, Bundle bundle, int requestCode){
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if(bundle != null){
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    /**
     * 带有退出动画
     */
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.in_from_right, R.anim.out_from_left);
    }

    /**
     * 默认退出
     */
    public void defaultFinish(){
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 取消绑定
        mBinder.unbind();
        //Activity销毁时，取消网络请求
        OkGo.getInstance().cancelTag(this);
        ActivityCollector.removeActivity(this);
    }
}
