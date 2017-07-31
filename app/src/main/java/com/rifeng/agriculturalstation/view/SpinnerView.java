package com.rifeng.agriculturalstation.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rifeng.agriculturalstation.R;
import com.rifeng.agriculturalstation.adapter.SpinnerProvinceAdapter;

public class SpinnerView extends RelativeLayout implements OnClickListener {

	private TextView content;
	private ImageView mIvArrow;

	private PopupWindow mWindow;
	private ListAdapter mAdapter;
	private SpinnerProvinceAdapter provinceAdapter;
	private OnItemClickListener mListener;
	private ListView mContentView;

	public SpinnerView(Context context) {
		this(context, null);
	}

	public SpinnerView(Context context, AttributeSet attrs) {
		super(context, attrs);

		// xml和class 绑定
		View.inflate(context, R.layout.spinner, this);

		content = (TextView) findViewById(R.id.spinner_text);
		mIvArrow = (ImageView) findViewById(R.id.iv_arrow);

		mIvArrow.setOnClickListener(this);
		content.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == mIvArrow || v == content) {
			clickArrow();
		}
	}

//	public void setAdapter(ListAdapter adapter) {
//		this.mAdapter = adapter;
//	}

	public void setAdapter(SpinnerProvinceAdapter adapter){
		this.provinceAdapter = adapter;
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		this.mListener = listener;
	}

	private void clickArrow() {
		// 点击箭头，需要弹出显示list数据的层

//		if (mAdapter == null) {
//			throw new RuntimeException("请调用setAapter()去设置数据");
//		}

		if (provinceAdapter == null) {
			throw new RuntimeException("请调用setAapter()去设置数据");
		}

		if (mWindow == null) {
			// contentView：显示的view
			// width height:popup宽和高

			mContentView = new ListView(getContext());

			// 设置数据
//			mContentView.setAdapter(mAdapter);// ---》adapter---》List《数据》
			mContentView.setAdapter(provinceAdapter);// ---》adapter---》List《数据》
			provinceAdapter.notifyDataSetChanged();
			mContentView.setBackgroundColor(Color.parseColor("#F0F0F0"));
			mContentView.setDividerHeight(0); // 去除ListView的分隔线
			int width = content.getWidth();
			int height = 500;

			mWindow = new PopupWindow(mContentView, width, height);
			// 设置获取焦点
			mWindow.setFocusable(true);

			// 设置边缘点击收起
			mWindow.setOutsideTouchable(true);
			mWindow.setBackgroundDrawable(new ColorDrawable());
		}

		// 设置item的点击事件
		mContentView.setOnItemClickListener(mListener);

		mWindow.showAsDropDown(content);
	}

	public void setText(String data) {
		content.setText(data);
	}

	public String getText(){
		return content.getText().toString().trim();
	}

	public void dismiss() {
		if (mWindow != null) {
			mWindow.dismiss();
		}
	}
}
