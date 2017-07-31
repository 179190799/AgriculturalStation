package com.rifeng.agriculturalstation.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.rifeng.agriculturalstation.R;

/**
 * 引导页
 *
 * Created by chw on 2016/10/14.
 */
public class GuidesActivity extends Activity implements ViewPager.OnPageChangeListener {

    /**
     * String imageUri = "http://site.com/image.png"; // from Web
     * String imageUri = "file:///mnt/sdcard/image.png"; // from SD card
     * String imageUri = "content://media/external/audio/albumart/13"; // from content provider
     * String imageUri = "assets://image.png"; // from assets
     * String imageUri = "drawable://" + R.drawable.image; // from drawables (only images, non-9patch)
     */
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private ViewPager viewPager;
    private static final int[] pics = {R.mipmap.guide1, R.mipmap.guide2, R.mipmap.guide3, R.mipmap.guide4};
    private ImageView[] points;
    private int currentIndex = 0;
    private LinearLayout dot_ll;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guides);
        // 透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        viewPager = (ViewPager) findViewById(R.id.guide_viewpager);
        dot_ll = (LinearLayout) findViewById(R.id.dot_ll);
        points = new ImageView[pics.length];
        // 圆点数与图片相等
        for(int i = 0; i < pics.length; i++){
            ImageView imageView = new ImageView(this);
            points[i] = imageView;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            params.rightMargin = 3;
            params.leftMargin = 3;

            imageView.setBackgroundResource(R.mipmap.dot_normal);
            dot_ll.addView(imageView, params);
        }
        viewPager.setCurrentItem(currentIndex);
        viewPager.setFocusable(true);
        viewPager.setAdapter(new ViewPagerAdapter());
        // 当ViewPager中页面的状态发生改变时调用
        viewPager.setOnPageChangeListener(this);

        setImageBackground(currentIndex);
    }

    /**
     * 设置选中的dot背景
     *
     * @param selectItems
     */
    private void setImageBackground(int selectItems) {
        for(int i = 0; i < points.length; i++){
            if(i == selectItems){
                // 选中状态
                points[i].setBackgroundResource(R.mipmap.dot_focus);
            }else {
                // 未选中状态
                points[i].setBackgroundResource(R.mipmap.dot_normal);
            }
        }
    }

    private class ViewPagerAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return pics.length;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

        }

        private class ViewHold {
            private ImageView ivPic;
            private FrameLayout start_fl;
            private TextView start_open;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View convertView = LayoutInflater.from(getApplication()).inflate(
                    R.layout.item_guide, null);
            ViewHold holder = new ViewHold();
            holder.ivPic = (ImageView) convertView.findViewById(R.id.givPic);
            holder.start_fl = (FrameLayout) convertView.findViewById(R.id.start_fl);
            holder.start_open = (TextView) convertView.findViewById(R.id.start_open);

            if (pics != null && pics.length > 0) {
//                holder.ivPic.setImageResource(pics[position]);
                imageLoader.displayImage("drawable://" + pics[position], holder.ivPic);
            }
            if (position == (pics.length - 1)) {
                holder.start_fl.setVisibility(View.VISIBLE);
                holder.start_open.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getApplication(), TabActivity.class));
                        GuidesActivity.this.finish();
                    }
                });
            }
            container.addView(convertView);
            return convertView;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        setImageBackground(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
