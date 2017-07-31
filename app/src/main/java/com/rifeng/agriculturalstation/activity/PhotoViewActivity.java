package com.rifeng.agriculturalstation.activity;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;
import android.widget.TextView;

import com.rifeng.agriculturalstation.BaseActivity;
import com.rifeng.agriculturalstation.R;
import com.rifeng.agriculturalstation.adapter.MyViewPagerAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnPageChange;

/**
 * 图片的缩放浏览
 * <p>
 * Created by chw on 2016/11/9.
 */
public class PhotoViewActivity extends BaseActivity {

    @BindView(R.id.imgs_viewpager)
    ViewPager mViewPager;
    @BindView(R.id.img_browse_back)
    ImageView img_browse_back;
    @BindView(R.id.serial_number)
    TextView serial_number;

    private int position;
    //    private int[] imgs;
    private String[] pics;
    private String flag;
    private ArrayList<String> selectedImg = new ArrayList<>();

    @Override
    protected int getContentViewId() {
        return R.layout.activity_photoview;
    }

    @Override
    protected void initData() {
        this.position = getIntent().getIntExtra("position", 0);
//        this.imgs = getIntent().getIntArrayExtra("imgs");
//        pics = getIntent().getStringArrayExtra("pics");
        flag = this.getIntent().getExtras().getString("flag");
        selectedImg = this.getIntent().getExtras().getStringArrayList("select");

        mViewPager.setOffscreenPageLimit(2);
        PagerAdapter adapter = new MyViewPagerAdapter(this, selectedImg);
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(position);

        if (flag.equals("multiple")) {
            serial_number.setText((position + 1) + "/" + selectedImg.size());
        } else {
            serial_number.setText((position + 1) + "/" + pics.length);
        }
    }

    @OnPageChange(value = R.id.imgs_viewpager, callback = OnPageChange.Callback.PAGE_SELECTED)
    public void onPageSelected(int position) {
        if (flag.equals("multiple")) {
            serial_number.setText((position + 1) + "/" + selectedImg.size());
        } else {
            serial_number.setText((position + 1) + "/" + pics.length);
        }
    }

    @OnClick(R.id.img_browse_back)
    public void onClick() {
        finish();
    }
}
