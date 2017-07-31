package com.rifeng.agriculturalstation.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * 自定义viewpager
 *
 * Created by chw on 2016/11/9.
 */
public class PhotoViewPager extends ViewPager {

    private float mTrans;
    private float mScale;
    /**
     * 最大的缩小比例
     */
    private static final float SCALE_MAX = 0.5f;
    /**
     * 保存position于对应的view
     */
    private HashMap<Integer, View> mChildrenViews = new LinkedHashMap<>();
    /**
     * 滑动时左边的元素
     */
    private View mLeft;
    /**
     * 滑动时右边的元素
     */
    private View mRight;

    public PhotoViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onPageScrolled(int position, float offset, int offsetPixels) {
        // 滑动特别小的距离时，我们认为没有动，可有可无的判断
        float effectOffset = isSmall(offset) ? 0 : offsetPixels;

        // 获取左边的View
        mLeft = findViewFromObject(position);
        // 获取右边的View
        mRight = findViewFromObject(position + 1);

        // 添加切换动画效果
        animateStack(mLeft, mRight, offset, offsetPixels);
        
        super.onPageScrolled(position, offset, offsetPixels);
    }

    public void setObjectForPosition(View view, int position) {
        mChildrenViews.put(position, view);
    }

    private void animateStack(View left, View right, float offset, int offsetPixels) {
        if(right != null){
            /**
             * 缩小比例 如果手指从右到左的滑动（切换到后一个）：0.0~1.0，即从一半到最大
             * 如果手指从左到右滑动（切换到前一个）：1.0~0.0，即从最大到一半
             */
            mScale = (1 - SCALE_MAX) * offset + SCALE_MAX;

            /**
             * x偏移量：如果手指从右到左的滑动（切换到后一个）：0-720 如果手指从左到右的滑动（切换到前一个）：720-0
             */
            mTrans = -getWidth() - getPageMargin() + offsetPixels;
            right.setScaleX(mScale);
            right.setScaleY(mScale);
            right.setTranslationX(mTrans);
        }
        if(left != null){
            left.bringToFront();
        }
    }

    /**
     * 通过位置获得对应的View
     *
     * @param position
     * @return
     */
    private View findViewFromObject(int position) {
        return mChildrenViews.get(position);
    }

    private boolean isSmall(float offset) {
        return Math.abs(offset) < 0.0001;
    }

	/*@Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }*/
}


































