package com.rifeng.agriculturalstation.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.rifeng.agriculturalstation.R;

/**
 * 滑动逐渐改变底部Tab图标、文本的颜色
 *
 * Created by chw on 2016/10/17.
 */
public class TabChangeView extends View {

    private int mColor = 0xFF45C01A;
    private Bitmap mIconBitmap;
    private String mText = "微信";
    private int mTextSize = (int) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics());

    private Canvas mCanvas;
    private Bitmap mBitmap;
    private Paint mPaint;

    private float mAlpha; // 颜色渐变时的透明度

    private Rect mIconRect; // 用来测量Icon
    private Rect mTextBound; // 用来测量Text
    private Paint mTextPaint;

    public TabChangeView(Context context) {
        this(context, null);
    }

    public TabChangeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 获取自定义属性的值
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public TabChangeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TabChangeView);

        int n = ta.getIndexCount();

        for(int i = 0; i < n; i++){
            int attr = ta.getIndex(i);
            switch (attr){
                case R.styleable.TabChangeView_tab_icon:
                    BitmapDrawable drawable = (BitmapDrawable) ta.getDrawable(attr);
                    mIconBitmap = drawable.getBitmap();
                    break;

                case R.styleable.TabChangeView_tab_color:
                    mColor = ta.getColor(attr, 0xFF45C01A);
                    break;

                case R.styleable.TabChangeView_tab_text:
                    mText = ta.getString(attr);
                    break;

                case R.styleable.TabChangeView_tab_text_size:
                    mTextSize = (int) ta.getDimension(attr, TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    break;
            }
        }
        // 释放资源
        ta.recycle();

        mTextBound = new Rect();
        mTextPaint = new Paint();
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(0Xff555555);
        mTextPaint.getTextBounds(mText, 0, mText.length(), mTextBound);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int iconWidth = Math.min(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(),
                getMeasuredHeight() - getPaddingTop() - getPaddingBottom() - mTextBound.height());

        // view宽度的一半 - 图标宽度的一半
        int left = getMeasuredWidth() / 2 - iconWidth / 2;
        // view高度的一半 - (文本的高度 + 图标宽度) / 2
        int top = getMeasuredHeight() / 2 - (mTextBound.height() + iconWidth) / 2;

        mIconRect = new Rect(left, top, left + iconWidth, top + iconWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mIconBitmap, null, mIconRect, null);

        int alpha = (int) Math.ceil(255 * mAlpha);

        // 内存去准备mBitmap，setAlpha，纯色，Xfermode，图标
        setupTargetBitmap(alpha);
        // 1、绘制原文本 2、绘制变色的文本
        drawSourceText(canvas, alpha);
        drawTargetText(canvas, alpha);

        canvas.drawBitmap(mBitmap, 0, 0, null);
    }

    /**
     * 绘制变色的文本
     *
     * @param canvas
     * @param alpha
     */
    private void drawTargetText(Canvas canvas, int alpha) {
        mTextPaint.setColor(mColor);
        mTextPaint.setAlpha(alpha);
        // view宽度的一半 - 文本宽度的一半
        int x = getMeasuredWidth() / 2 - mTextBound.width() / 2;
        // 图标底部距离 + 文本高度
        int y = mIconRect.bottom + mTextBound.height();
        canvas.drawText(mText, x, y, mTextPaint);
    }

    /**
     * 绘制原文本
     *
     * @param canvas
     * @param alpha
     */
    private void drawSourceText(Canvas canvas, int alpha) {
        mTextPaint.setColor(0xff333333);
        mTextPaint.setAlpha(255 - alpha);
        // view宽度的一半 - 文本宽度的一半
        int x = getMeasuredWidth() / 2 - mTextBound.width() / 2;
        // 图标底部距离 + 文本高度
        int y = mIconRect.bottom + mTextBound.height();
        canvas.drawText(mText, x, y, mTextPaint);
    }

    /**
     * 在内存中绘制可变色的Icon
     *
     * @param alpha
     */
    private void setupTargetBitmap(int alpha) {
        mBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mPaint = new Paint();
        mPaint.setColor(mColor);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setAlpha(alpha);
        mCanvas.drawRect(mIconRect, mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mPaint.setAlpha(255);
        mCanvas.drawBitmap(mIconBitmap, null, mIconRect, mPaint);
    }

    private static final String INSTANCE_STATUS = "instance_status";
    private static final String STATUS_ALPHA = "status_alpha";

    /**
     * 存储View的状态
     *
     * @return
     */
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        // 存储系统的InstanceState
        bundle.putParcelable(INSTANCE_STATUS, super.onSaveInstanceState());
        // 存储自己的InstanceState
        bundle.putFloat(STATUS_ALPHA, mAlpha);
        return bundle;
    }

    /**
     * 恢复View的状态
     *
     * @param state
     */
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if(state instanceof Bundle){
            Bundle bundle = (Bundle) state;
            mAlpha = bundle.getFloat(STATUS_ALPHA);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATUS));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    /**
     * 公布一个方法，用于设置透明度
     *
     * @param alpha
     */
    public void setIconAlpha(float alpha){
        this.mAlpha = alpha;
        invalidateView();
    }

    /**
     * 重绘
     */
    private void invalidateView() {
        if(Looper.getMainLooper() == Looper.myLooper()){
            // 在UI线程中
            invalidate();
        }else {
            // 在UI非线程中
            postInvalidate();
        }
    }
}
