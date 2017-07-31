package com.rifeng.agriculturalstation.adapter;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.rifeng.agriculturalstation.BaseApplication;
import com.rifeng.agriculturalstation.R;
import com.rifeng.agriculturalstation.utils.Urls;

import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class MyViewPagerAdapter extends PagerAdapter {

//    String[] pics;
    private List<String> pics;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    Activity mContext;

    public MyViewPagerAdapter(Activity context, List<String> imgs) {
        this.mContext = context;
        this.pics = imgs;
    }

    @Override
    public int getCount() { // 获得size
        return pics.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        String imgUrl = pics.get(position);
        LinearLayout view = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.img_browse, null);
        PhotoView img = (PhotoView) view.findViewById(R.id.img_plan);
        img.setTag(imgUrl);
//        ImageLoader.getInstance().displayImage("drawable://" + imgs[position], img);
        imageLoader.displayImage(Urls.BASE_IMGURL + pics.get(position), img);
        ((ViewPager) container).addView(view);
        // 点击图片
        img.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
            }
        });
        //点击图片外
        img.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                mContext.finish();
            }
        });
        return view;
    }
}