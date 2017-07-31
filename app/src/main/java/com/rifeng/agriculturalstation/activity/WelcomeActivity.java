package com.rifeng.agriculturalstation.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.rifeng.agriculturalstation.R;
import com.rifeng.agriculturalstation.utils.AppInfoUtil;
import com.rifeng.agriculturalstation.utils.DeviceId;
import com.rifeng.agriculturalstation.utils.MD5;
import com.rifeng.agriculturalstation.utils.SharedPreferencesUtil;
import com.rifeng.agriculturalstation.utils.ToastUtil;

import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;

/**
 * 欢迎or广告页
 * <p>
 * Created by chw on 2016/10/14.
 */
public class WelcomeActivity extends Activity {

    private ImageLoader imageLoader = ImageLoader.getInstance();
    private static final String VERSION_NAME = "VERSION_NAME";
    private static final String VERSION_CODE = "VERSION_CODE";


    //设置是否自启动
    private boolean isGoing ;

    private ImageView wel_bg; // 背景图
    private TextView skip_tv; // 跳过

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                startActivity(new Intent(WelcomeActivity.this, TabActivity.class));
                finish();
            }
            if (msg.what == 2) {
                if (isGoing) {
                    startActivity(new Intent(WelcomeActivity.this, TabActivity.class));
//                overridePendingTransition(R.anim.in_from_right, R.anim.out_from_right);
                    finish();
                }

            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 生成设备的唯一ID，并存储到SharedPreferences中
        //JPushInterface.setDebugMode(true);
        //JPushInterface.init(this);

        createTokenAndSave();

        String oldVersionName = (String) SharedPreferencesUtil.get(this, VERSION_NAME, "");
        int oldVersionCode = (int) SharedPreferencesUtil.get(this, VERSION_CODE, -1);

        String newVersionName = AppInfoUtil.getVersionName(this);
        int newVersionCode = AppInfoUtil.getVersionCode(this);
        // 是否是第一次安装并运行app
        if (!TextUtils.equals(oldVersionName, newVersionName) && newVersionCode > oldVersionCode) {
            SharedPreferencesUtil.put(this, VERSION_NAME, newVersionName);
            SharedPreferencesUtil.put(this, VERSION_CODE, newVersionCode);
            startActivity(new Intent(this, GuidesActivity.class));
            finish();
        } else {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setContentView(R.layout.activity_welcome);
            wel_bg = (ImageView) findViewById(R.id.wel_bg);
            skip_tv = (TextView) findViewById(R.id.skip_tv);
            imageLoader.displayImage("drawable://" + R.mipmap.guide1, wel_bg, DisplayImageOptions.createSimple());

            isGoing = starActivityByButton();
            mHandler.sendEmptyMessageDelayed(2, 3000);
        // 加载欢迎页
            //loadWelcome();
        }
    }

    private boolean starActivityByButton() {

        isGoing = true;
        //设置监听事件
        skip_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("TAG","执行onClick");
                //mHandler.removeMessages(2);
                isGoing = false;
                startActivity(new Intent(WelcomeActivity.this, TabActivity.class));
//                    overridePendingTransition(R.anim.in_from_right, R.anim.out_from_right);
                finish();
            }
        });

        return isGoing;
    }


    /**
     * 生成设备的唯一ID，并存储到SharedPreferences中
     */
    private void createTokenAndSave() {
        String strMD5Token = MD5.getMD5(DeviceId.getDeviceId(this));
        SharedPreferencesUtil.put(this, "token", strMD5Token);
    }

    /**
     * 加载欢迎页
     */
    private void loadWelcome() {
        startActivity(new Intent(WelcomeActivity.this, TabActivity.class));
        finish();
    }
}
