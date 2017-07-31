package com.rifeng.agriculturalstation.utils;

import android.content.Context;
import android.widget.ImageView;

import com.youth.banner.loader.ImageLoader;

/**
 * Created by chw on 2017/3/9.
 */
public class ViewPagerImageLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(Urls.BASE_IMGURL + (String) path, imageView);
    }
}
















































