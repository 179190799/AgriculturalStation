package com.rifeng.agriculturalstation;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.baidu.mapapi.SDKInitializer;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.store.PersistentCookieStore;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.rifeng.agriculturalstation.service.LocationService;
import com.rifeng.agriculturalstation.utils.LogUtil;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.File;
import java.util.logging.Level;

import cn.jpush.android.api.JPushInterface;

/**
 * 用于存放全局变量和公用的资源等
 *
 * Created by chw on 2016/10/17.
 */
public class BaseApplication extends Application {

    public static File cacheFile;
    /**
     * 百度定位
     */
    public LocationService locationService;
//    private static DisplayImageOptions imageOptions;
    // IWXAPI 是第三方app和微信通信的openapi接口
    public static IWXAPI iwxapi;

    @Override
    public void onCreate() {
        super.onCreate();

        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        Runtime rt = Runtime.getRuntime();
        long maxMemory = rt.maxMemory();
        LogUtil.i("maxMemory:" + Long.toString(maxMemory / (1024 * 1024)));

        regToWx();

        /***
         * 初始化定位sdk，建议在Application中创建
         */
        locationService = new LocationService(getApplicationContext());
        SDKInitializer.initialize(getApplicationContext());

        // 判断存储卡是否可用
        initAppDir();

        // 初始化ImageLoader设置
//        imageOptions = getImageOptions();
        initImageLoader(getApplicationContext());

        //必须调用初始化
        OkGo.init(this);
        //以下设置的所有参数是全局参数,同样的参数可以在请求的时候再设置一遍,那么对于该请求来讲,请求中的参数会覆盖全局参数
        //好处是全局参数统一,特定请求可以特别定制参数
        try {
            //以下都不是必须的，根据需要自行选择,一般来说只需要 debug,缓存相关,cookie相关的 就可以了
            OkGo.getInstance()

                    // 打开该调试开关,打印级别INFO,并不是异常,是为了显眼,不需要就不要加入该行
                    // 最后的true表示是否打印okgo的内部异常，一般打开方便调试错误
                    .debug("OkGo", Level.INFO, true)

                    //如果使用默认的 60秒,以下三行也不需要传
                    .setConnectTimeout(OkGo.DEFAULT_MILLISECONDS)  //全局的连接超时时间
                    .setReadTimeOut(OkGo.DEFAULT_MILLISECONDS)     //全局的读取超时时间
                    .setWriteTimeOut(OkGo.DEFAULT_MILLISECONDS)    //全局的写入超时时间

                    //可以全局统一设置缓存模式,默认是不使用缓存,可以不传,具体其他模式看 github 介绍 https://github.com/jeasonlzy/
                    .setCacheMode(CacheMode.NO_CACHE)

                    //可以全局统一设置缓存时间,默认永不过期,具体使用方法看 github 介绍
                    .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)

                    //可以全局统一设置超时重连次数,默认为三次,那么最差的情况会请求4次(一次原始请求,三次重连请求),不需要可以设置为0
                    .setRetryCount(3)

                    //如果不想让框架管理cookie（或者叫session的保持）,以下不需要
//                .setCookieStore(new MemoryCookieStore())            //cookie使用内存缓存（app退出后，cookie消失）
                    .setCookieStore(new PersistentCookieStore())        //cookie持久化存储，如果cookie不过期，则一直有效

                    //可以设置https的证书,以下几种方案根据需要自己设置
                    .setCertificates();                                  //方法一：信任所有证书,不安全有风险

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void regToWx() {
        // 通过 WXAPIFactory工厂，获取IWXAPI的实例
        iwxapi = WXAPIFactory.createWXAPI(this, "APP_ID", true);

        // 将应用的appId注册到微信
        iwxapi.registerApp("APP_ID");
    }

    private static DisplayImageOptions getImageOptions() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.no_picture) // 设置图片在下载期间显示的图片
                .showImageForEmptyUri(R.mipmap.no_picture) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.no_picture) // 设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build(); // 构建完成
        return options;
    }

    private static void initImageLoader(Context context) {
        File cacheDir = StorageUtils.getOwnCacheDirectory(context, "AgriculturalStationCache");
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .memoryCacheExtraOptions(480, 800)//保存的每个缓存文件的最大长宽
                .threadPoolSize(3)//线程池内加载的数量
                .threadPriority(Thread.NORM_PRIORITY - 2)//设置当前线程的优先级
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))//可以通过自己的内存缓存实现
                .memoryCacheSize(2 * 1024 * 1024)//内存缓存的最大值
                .discCacheSize(50 * 1024 * 1024)//sd卡缓存的最大值
                .discCacheFileNameGenerator(new Md5FileNameGenerator())//将保存的时候的URI名称用MD5加密
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .discCacheFileCount(100)//缓存文件的数量
                .discCache(new UnlimitedDiscCache(cacheDir))//自定义缓存路径
//                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .defaultDisplayImageOptions(getImageOptions())
                .imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 1000))//超时时间
                .writeDebugLogs()//打印debug、log
                .build();//开始构建

        //全局初始化此配置
        ImageLoader.getInstance().init(config);
    }

    /**
     * 创建缓存路径
     */
    private void initAppDir() {
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            // 在sd卡下创建缓存目录
            cacheFile = new File(Environment.getExternalStorageDirectory(), "AgriculturalStationCache");
        }else {
            // sd卡不可用，在app包名下创建缓存目录
            cacheFile = new File(getApplicationContext().getFilesDir(), "AgriculturalStationCache");
        }
        if(!cacheFile.exists()){ // 判断文件夹是否存在
            cacheFile.mkdirs();
        }
    }
}








































